package de.artemis.commontrades.trade;

import de.artemis.commontrades.CommonTrades;
import de.artemis.commontrades.config.CommonTradesConfig;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;

public final class TradePoolCache {
    private static final float PRICE_MULTIPLIER = 0.05F;
    private static final int VILLAGER_XP = 1;

    private static Map<TradeCategory, List<TradeEntry>> entriesByCategory = emptyPools();
    private static boolean built;
    private static boolean dirty = true;

    private TradePoolCache() {
    }

    public static void onTagsLoaded(RegistryAccess registries) {
        markDirty();
    }

    public static synchronized void markDirty() {
        dirty = true;
    }

    public static synchronized void rebuild() {
        Map<TradeCategory, List<TradeEntry>> rebuilt = new EnumMap<>(TradeCategory.class);
        Set<ResourceLocation> itemBlacklist = CommonTradesConfig.itemBlacklist();
        Set<String> modBlacklist = CommonTradesConfig.modBlacklist();
        List<TagKey<Item>> tagBlacklist = CommonTradesConfig.tagBlacklist();
        Set<Item> registeredWanderingTradeItems = RegisteredWanderingTradeInspector.registeredResultItems();

        Map<ResourceLocation, TradeEntry> discovered = new LinkedHashMap<>();
        for (TradeCategory category : TradeCategory.values()) {
            if (CommonTradesConfig.enabled() && CommonTradesConfig.isCategoryEnabled(category)) {
                for (TagKey<Item> sourceTag : category.sourceTags()) {
                    for (Holder<Item> holder : Registry.ITEM.getTagOrEmpty(sourceTag)) {
                        Item item = holder.value();
                        ResourceLocation id = Registry.ITEM.getKey(item);
                        if (isEligible(holder, item, id, itemBlacklist, modBlacklist, tagBlacklist, registeredWanderingTradeItems)) {
                            discovered.putIfAbsent(id, new TradeEntry(category, item, id));
                        }
                    }
                }
            }
        }

        int total = 0;
        for (TradeCategory category : TradeCategory.values()) {
            List<TradeEntry> entries = discovered.values().stream()
                    .filter(entry -> entry.category() == category)
                    .toList();
            rebuilt.put(category, entries);
            total += entries.size();
            CommonTrades.LOGGER.debug("Common Trades found {} eligible {}.", entries.size(), category.logName());
        }

        entriesByCategory = Map.copyOf(rebuilt);
        built = true;
        dirty = false;
        if (CommonTradesConfig.isLoaded()) {
            CommonTrades.LOGGER.info("Common Trades found {} eligible modded items across {} categories.", total, TradeCategory.values().length);
        } else {
            CommonTrades.LOGGER.debug("Common Trades found {} eligible modded items across {} categories before server config load.", total, TradeCategory.values().length);
        }
    }

    public static synchronized void ensureBuilt() {
        if (!built || dirty) {
            rebuild();
        }
    }

    public static synchronized boolean hasEntries() {
        return entriesByCategory.values().stream().anyMatch(entries -> !entries.isEmpty());
    }

    public static synchronized Map<TradeCategory, List<TradeEntry>> snapshotEntriesByCategory() {
        ensureBuilt();
        Map<TradeCategory, List<TradeEntry>> snapshot = new EnumMap<>(TradeCategory.class);
        for (TradeCategory category : TradeCategory.values()) {
            snapshot.put(category, List.copyOf(entriesByCategory.getOrDefault(category, List.of())));
        }
        return Map.copyOf(snapshot);
    }

    public static List<MerchantOffer> createOffers(Random random, Set<Item> usedItems, int maxOffers) {
        List<MerchantOffer> offers = new ArrayList<>();
        if (maxOffers <= 0) {
            return offers;
        }

        ensureBuilt();
        Set<Item> blockedItems = new HashSet<>(usedItems);
        while (offers.size() < maxOffers) {
            Optional<SelectedTrade> trade = createOffer(random, blockedItems);
            if (trade.isEmpty()) {
                break;
            }

            SelectedTrade selectedTrade = trade.get();
            blockedItems.add(selectedTrade.item());
            offers.add(selectedTrade.offer());
        }
        return offers;
    }

    static Optional<SelectedTrade> createOffer(Random random, Set<Item> usedItems) {
        ensureBuilt();
        if (!CommonTradesConfig.enabled() || !hasEntries()) {
            return Optional.empty();
        }

        List<TradeCategory> categories = availableCategories(usedItems);
        if (categories.isEmpty()) {
            return Optional.empty();
        }

        int attempts = Math.max(16, categories.size() * 8);
        for (int i = 0; i < attempts; i++) {
            TradeCategory category = pickCategory(categories, random);
            List<TradeEntry> entries = entriesByCategory.getOrDefault(category, List.of());
            if (entries.isEmpty()) {
                continue;
            }

            TradeEntry entry = entries.get(random.nextInt(entries.size()));
            if (usedItems.contains(entry.item())) {
                continue;
            }

            Optional<SelectedTrade> trade = makeOffer(entry, random);
            if (trade.isPresent()) {
                return trade;
            }
        }

        List<TradeEntry> remaining = new ArrayList<>();
        for (TradeCategory category : categories) {
            for (TradeEntry entry : entriesByCategory.getOrDefault(category, List.of())) {
                if (!usedItems.contains(entry.item())) {
                    remaining.add(entry);
                }
            }
        }

        while (!remaining.isEmpty()) {
            TradeEntry entry = remaining.remove(random.nextInt(remaining.size()));
            Optional<SelectedTrade> trade = makeOffer(entry, random);
            if (trade.isPresent()) {
                return trade;
            }
        }

        return Optional.empty();
    }

    private static boolean isEligible(
            Holder<Item> holder,
            Item item,
            ResourceLocation id,
            Set<ResourceLocation> itemBlacklist,
            Set<String> modBlacklist,
            List<TagKey<Item>> tagBlacklist,
            Set<Item> registeredWanderingTradeItems) {
        if (item == Items.AIR || "minecraft".equals(id.getNamespace())) {
            return false;
        }
        if (itemBlacklist.contains(id) || modBlacklist.contains(id.getNamespace())) {
            return false;
        }
        if (registeredWanderingTradeItems.contains(item)) {
            return false;
        }
        if (holder.is(CommonTradeTags.BLACKLIST) || holder.is(CommonTradeTags.HIDDEN_FROM_RECIPE_VIEWERS)) {
            return false;
        }
        for (TagKey<Item> blacklistedTag : tagBlacklist) {
            if (holder.is(blacklistedTag)) {
                return false;
            }
        }

        ItemStack defaultStack = item.getDefaultInstance();
        return !defaultStack.isEmpty()
                && defaultStack.getMaxStackSize() > 1
                && !defaultStack.isDamageableItem();
    }

    private static List<TradeCategory> availableCategories(Set<Item> usedItems) {
        List<TradeCategory> categories = new ArrayList<>();
        for (TradeCategory category : TradeCategory.values()) {
            if (!CommonTradesConfig.isCategoryEnabled(category)) {
                continue;
            }

            boolean hasUnusedEntry = entriesByCategory.getOrDefault(category, List.of()).stream()
                    .anyMatch(entry -> !usedItems.contains(entry.item()));
            if (hasUnusedEntry) {
                categories.add(category);
            }
        }
        return categories;
    }

    private static TradeCategory pickCategory(List<TradeCategory> categories, Random random) {
        int totalWeight = categories.stream().mapToInt(TradeCategory::selectionWeight).sum();
        int selectedWeight = random.nextInt(totalWeight);
        for (TradeCategory category : categories) {
            selectedWeight -= category.selectionWeight();
            if (selectedWeight < 0) {
                return category;
            }
        }
        return categories.get(categories.size() - 1);
    }

    private static Optional<SelectedTrade> makeOffer(TradeEntry entry, Random random) {
        TradeCategory category = entry.category();
        int count = category.minOutputCount();
        if (category.maxOutputCount() > category.minOutputCount()) {
            count += random.nextInt(category.maxOutputCount() - category.minOutputCount() + 1);
        }

        ItemStack forSale = new ItemStack(entry.item(), count);
        MerchantOffer offer = new MerchantOffer(
                new ItemStack(Items.EMERALD, CommonTradesConfig.emeraldCost(category)),
                forSale,
                category.maxUses(),
                VILLAGER_XP,
                PRICE_MULTIPLIER);
        return Optional.of(new SelectedTrade(entry.item(), entry.id(), offer));
    }

    private static Map<TradeCategory, List<TradeEntry>> emptyPools() {
        Map<TradeCategory, List<TradeEntry>> pools = new EnumMap<>(TradeCategory.class);
        for (TradeCategory category : TradeCategory.values()) {
            pools.put(category, List.of());
        }
        return Map.copyOf(pools);
    }
}

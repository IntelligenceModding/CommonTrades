package de.artemis.commontrades.trade;

import de.artemis.commontrades.CommonTrades;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.apache.commons.lang3.tuple.Pair;

public final class RegisteredWanderingTradeInspector {
    private RegisteredWanderingTradeInspector() {
    }

    public static Set<Item> registeredResultItems() {
        Set<Item> items = new HashSet<>();
        wanderingTradeListings().forEach(tradeListings -> {
            for (VillagerTrades.ItemListing listing : tradeListings) {
                inspectKnownListing(listing).map(InspectedOffer::item).ifPresent(items::add);
            }
        });
        return Set.copyOf(items);
    }

    public static Optional<InspectedOffer> inspectKnownListing(VillagerTrades.ItemListing listing) {
        String className = listing.getClass().getName();
        try {
            if (className.equals("net.minecraft.world.entity.npc.VillagerTrades$ItemsForEmeralds")) {
                ItemStack result = copyStack(field(listing, "itemStack", ItemStack.class));
                return knownOffer(result, field(listing, "emeraldCost", Integer.class), field(listing, "maxUses", Integer.class), "");
            }
            if (className.equals("net.minecraft.world.entity.npc.VillagerTrades$ItemsAndEmeraldsToItems")) {
                ItemStack result = copyStack(field(listing, "toItem", ItemStack.class));
                return knownOffer(result, field(listing, "emeraldCost", Integer.class), field(listing, "maxUses", Integer.class), "");
            }
            if (className.equals("net.minecraft.world.entity.npc.VillagerTrades$SuspiciousStewForEmerald")) {
                return knownOffer(new ItemStack(Items.SUSPICIOUS_STEW), 1, 12, "");
            }
            if (className.equals("net.minecraft.world.entity.npc.VillagerTrades$TippedArrowForItemsAndEmeralds")) {
                ItemStack result = copyStack(field(listing, "toItem", ItemStack.class));
                result.setCount(field(listing, "toCount", Integer.class));
                return knownOffer(result, field(listing, "emeraldCost", Integer.class), field(listing, "maxUses", Integer.class), "dynamic potion");
            }
            if (listing instanceof net.neoforged.neoforge.common.BasicItemListing) {
                ItemStack result = copyStack(field(listing, "forSale", ItemStack.class));
                Integer emeraldCost = emeraldCostFromBasicListing(listing);
                if (emeraldCost == null) {
                    return Optional.empty();
                }
                return knownOffer(result, emeraldCost, field(listing, "maxTrades", Integer.class), "");
            }
        } catch (ReflectiveOperationException | ClassCastException exception) {
            CommonTrades.LOGGER.debug("Could not inspect Wandering Trader trade listing {}", listing.getClass().getName(), exception);
        }
        return Optional.empty();
    }

    public static Set<VillagerTrades.ItemListing> vanillaListings() {
        Set<VillagerTrades.ItemListing> listings = new HashSet<>();
        try {
            Class<?> managerClass = Class.forName("net.neoforged.neoforge.common.VillagerTradingManager");
            Field field = managerClass.getDeclaredField("WANDERER_TRADES");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<Pair<VillagerTrades.ItemListing[], Integer>> vanillaTrades =
                    (List<Pair<VillagerTrades.ItemListing[], Integer>>) field.get(null);
            for (Pair<VillagerTrades.ItemListing[], Integer> tradeGroup : vanillaTrades) {
                listings.addAll(Arrays.asList(tradeGroup.getLeft()));
            }
        } catch (ReflectiveOperationException | ClassCastException exception) {
            CommonTrades.LOGGER.debug("Could not inspect vanilla Wandering Trader trade baseline.", exception);
            wanderingTradeListings().forEach(tradeListings -> {
                for (VillagerTrades.ItemListing listing : tradeListings) {
                    if (listing.getClass().getName().startsWith("net.minecraft.")) {
                        listings.add(listing);
                    }
                }
            });
        }
        return listings;
    }

    public static List<VillagerTrades.ItemListing[]> wanderingTradeListings() {
        return VillagerTrades.WANDERING_TRADER_TRADES.stream()
                .map(Pair::getLeft)
                .toList();
    }

    private static Optional<InspectedOffer> knownOffer(ItemStack result, int emeraldCost, int maxUses, String note) {
        if (result.isEmpty()) {
            return Optional.empty();
        }
        ResourceLocation resultId = BuiltInRegistries.ITEM.getKey(result.getItem());
        if (resultId == null) {
            return Optional.empty();
        }
        return Optional.of(new InspectedOffer(resultId, result.getItem(), result.getCount(), emeraldCost, maxUses, note));
    }

    private static Integer emeraldCostFromBasicListing(VillagerTrades.ItemListing listing) throws ReflectiveOperationException {
        ItemStack price = field(listing, "price", ItemStack.class);
        ItemStack price2 = field(listing, "price2", ItemStack.class);
        if (price.is(Items.EMERALD)) {
            return price.getCount();
        }
        if (price2.is(Items.EMERALD)) {
            return price2.getCount();
        }
        return null;
    }

    private static ItemStack copyStack(ItemStack stack) {
        return stack.copy();
    }

    private static <T> T field(Object target, String name, Class<T> type) throws ReflectiveOperationException {
        Field field = findField(target.getClass(), name);
        field.setAccessible(true);
        Object value = field.get(target);
        if (type == Integer.class && value instanceof Integer integer) {
            return type.cast(integer);
        }
        return type.cast(value);
    }

    private static Field findField(Class<?> type, String name) throws NoSuchFieldException {
        Class<?> current = type;
        while (current != null) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException exception) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name);
    }

    public record InspectedOffer(ResourceLocation resultId, Item item, int amount, int emeraldCost, int maxUses, String note) {
    }
}

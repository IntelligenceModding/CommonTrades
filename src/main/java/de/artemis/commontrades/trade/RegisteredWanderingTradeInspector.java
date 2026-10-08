package de.artemis.commontrades.trade;

import de.artemis.commontrades.CommonTrades;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public final class RegisteredWanderingTradeInspector {
    private RegisteredWanderingTradeInspector() {
    }

    public static Set<Item> registeredResultItems() {
        Set<Item> items = new HashSet<>();
        VillagerTrades.WANDERING_TRADER_TRADES.values().forEach(tradeListings -> {
            for (VillagerTrades.ITrade listing : tradeListings) {
                inspectKnownListing(listing).map(InspectedOffer::item).ifPresent(items::add);
            }
        });
        return Collections.unmodifiableSet(items);
    }

    public static Optional<InspectedOffer> inspectKnownListing(VillagerTrades.ITrade listing) {
        String className = listing.getClass().getName();
        try {
            if (className.equals("net.minecraft.entity.merchant.villager.VillagerTrades$ItemsForEmeraldsTrade")) {
                ItemStack result = copyStack(field(listing, "itemStack", ItemStack.class));
                return knownOffer(result, field(listing, "emeraldCost", Integer.class), field(listing, "maxUses", Integer.class), "");
            }
            if (className.equals("net.minecraft.entity.merchant.villager.VillagerTrades$ItemsForEmeraldsAndItemsTrade")) {
                ItemStack result = copyStack(field(listing, "toItem", ItemStack.class));
                return knownOffer(result, field(listing, "emeraldCost", Integer.class), field(listing, "maxUses", Integer.class), "");
            }
            if (className.equals("net.minecraft.entity.merchant.villager.VillagerTrades$SuspiciousStewForEmeraldTrade")) {
                return knownOffer(new ItemStack(Items.SUSPICIOUS_STEW), 1, 12, "");
            }
            if (className.equals("net.minecraft.entity.merchant.villager.VillagerTrades$ItemWithPotionForEmeraldsAndItemsTrade")) {
                ItemStack result = copyStack(field(listing, "toItem", ItemStack.class));
                result.setCount(field(listing, "toCount", Integer.class));
                return knownOffer(result, field(listing, "emeraldCost", Integer.class), field(listing, "maxUses", Integer.class), "dynamic potion");
            }
            if (isBasicItemListingLike(listing)) {
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

    public static Set<VillagerTrades.ITrade> vanillaListings() {
        Set<VillagerTrades.ITrade> listings = new HashSet<>();
        try {
            Class<?> managerClass = Class.forName("net.minecraftforge.common.VillagerTradingManager");
            Field field = managerClass.getDeclaredField("WANDERER_TRADES");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Int2ObjectMap<VillagerTrades.ITrade[]> vanillaTrades =
                    (Int2ObjectMap<VillagerTrades.ITrade[]>) field.get(null);
            for (VillagerTrades.ITrade[] tradeListings : vanillaTrades.values()) {
                listings.addAll(Arrays.asList(tradeListings));
            }
        } catch (ReflectiveOperationException | ClassCastException exception) {
            CommonTrades.LOGGER.debug("Could not inspect vanilla Wandering Trader trade baseline.", exception);
            VillagerTrades.WANDERING_TRADER_TRADES.values().forEach(tradeListings -> {
                for (VillagerTrades.ITrade listing : tradeListings) {
                    if (listing.getClass().getName().startsWith("net.minecraft.")) {
                        listings.add(listing);
                    }
                }
            });
        }
        return listings;
    }

    private static Optional<InspectedOffer> knownOffer(ItemStack result, int emeraldCost, int maxUses, String note) {
        if (result.isEmpty()) {
            return Optional.empty();
        }
        ResourceLocation resultId = Registry.ITEM.getKey(result.getItem());
        if (resultId == null) {
            return Optional.empty();
        }
        return Optional.of(new InspectedOffer(resultId, result.getItem(), result.getCount(), emeraldCost, maxUses, note));
    }

    private static Integer emeraldCostFromBasicListing(VillagerTrades.ITrade listing) throws ReflectiveOperationException {
        ItemStack price = field(listing, "price", ItemStack.class);
        ItemStack price2 = field(listing, "price2", ItemStack.class);
        if (price.getItem() == Items.EMERALD) {
            return price.getCount();
        }
        if (price2.getItem() == Items.EMERALD) {
            return price2.getCount();
        }
        return null;
    }

    private static boolean isBasicItemListingLike(VillagerTrades.ITrade listing) {
        Class<?> type = listing.getClass();
        return hasField(type, "forSale")
                && hasField(type, "price")
                && hasField(type, "price2")
                && hasField(type, "maxTrades");
    }

    private static ItemStack copyStack(ItemStack stack) {
        return stack.copy();
    }

    private static <T> T field(Object target, String name, Class<T> type) throws ReflectiveOperationException {
        Field field = findField(target.getClass(), name);
        field.setAccessible(true);
        Object value = field.get(target);
        if (type == Integer.class && value instanceof Integer) {
            return type.cast(value);
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

    private static boolean hasField(Class<?> type, String name) {
        try {
            findField(type, name);
            return true;
        } catch (NoSuchFieldException exception) {
            return false;
        }
    }

    public static final class InspectedOffer {
        private final ResourceLocation resultId;
        private final Item item;
        private final int amount;
        private final int emeraldCost;
        private final int maxUses;
        private final String note;

        public InspectedOffer(ResourceLocation resultId, Item item, int amount, int emeraldCost, int maxUses, String note) {
            this.resultId = resultId;
            this.item = item;
            this.amount = amount;
            this.emeraldCost = emeraldCost;
            this.maxUses = maxUses;
            this.note = note;
        }

        public ResourceLocation resultId() {
            return resultId;
        }

        public Item item() {
            return item;
        }

        public int amount() {
            return amount;
        }

        public int emeraldCost() {
            return emeraldCost;
        }

        public int maxUses() {
            return maxUses;
        }

        public String note() {
            return note;
        }
    }
}

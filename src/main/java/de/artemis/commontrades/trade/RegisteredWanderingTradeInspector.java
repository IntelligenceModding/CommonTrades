package de.artemis.commontrades.trade;

import de.artemis.commontrades.CommonTrades;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.npc.villager.VillagerTrades;
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
            if (className.equals("net.minecraft.world.entity.npc.villager.VillagerTrades$ItemsForEmeralds")) {
                ItemStack result = copyStack(field(listing, "itemStack", ItemStack.class));
                return knownOffer(result, field(listing, "emeraldCost", Integer.class), field(listing, "maxUses", Integer.class), "");
            }
            if (className.equals("net.minecraft.world.entity.npc.villager.VillagerTrades$ItemsAndEmeraldsToItems")) {
                ItemStack result = copyStack(field(listing, "toItem", ItemStack.class));
                return knownOffer(result, field(listing, "emeraldCost", Integer.class), field(listing, "maxUses", Integer.class), "");
            }
            if (className.equals("net.minecraft.world.entity.npc.villager.VillagerTrades$SuspiciousStewForEmerald")) {
                return knownOffer(new ItemStack(Items.SUSPICIOUS_STEW), 1, 12, "");
            }
            if (className.equals("net.minecraft.world.entity.npc.villager.VillagerTrades$TippedArrowForItemsAndEmeralds")) {
                ItemStack result = copyStack(field(listing, "toItem", ItemStack.class));
                result.setCount(field(listing, "toCount", Integer.class));
                return knownOffer(result, field(listing, "emeraldCost", Integer.class), field(listing, "maxUses", Integer.class), "dynamic potion");
            }
        } catch (ReflectiveOperationException | ClassCastException exception) {
            CommonTrades.LOGGER.debug("Could not inspect Wandering Trader trade listing {}", listing.getClass().getName(), exception);
        }
        return Optional.empty();
    }

    public static Set<VillagerTrades.ItemListing> vanillaListings() {
        Set<VillagerTrades.ItemListing> listings = new HashSet<>();
        wanderingTradeListings().forEach(tradeListings -> {
            for (VillagerTrades.ItemListing listing : tradeListings) {
                if (listing.getClass().getName().startsWith("net.minecraft.")) {
                    listings.add(listing);
                }
            }
        });
        return Set.copyOf(listings);
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
        Identifier resultId = BuiltInRegistries.ITEM.getKey(result.getItem());
        if (resultId == null) {
            return Optional.empty();
        }
        return Optional.of(new InspectedOffer(resultId, result.getItem(), result.getCount(), emeraldCost, maxUses, note));
    }

    private static ItemStack copyStack(ItemStack stack) {
        return stack.copy();
    }

    private static <T> T field(Object target, String name, Class<T> type) throws ReflectiveOperationException {
        Field field = findField(target.getClass(), name);
        field.setAccessible(true);
        return type.cast(field.get(target));
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

    public record InspectedOffer(Identifier resultId, Item item, int amount, int emeraldCost, int maxUses, String note) {
    }
}

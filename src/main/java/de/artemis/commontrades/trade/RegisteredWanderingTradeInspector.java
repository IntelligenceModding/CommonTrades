package de.artemis.commontrades.trade;

import de.artemis.commontrades.CommonTrades;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;

public final class RegisteredWanderingTradeInspector {
    private static final int GENERATED_LISTING_SAMPLE_COUNT = 64;

    private RegisteredWanderingTradeInspector() {
    }

    public static Set<Item> registeredResultItems() {
        Set<Item> items = new HashSet<>();
        VillagerTrades.WANDERING_TRADER_TRADES.values().forEach(tradeListings -> {
            for (VillagerTrades.ItemListing listing : tradeListings) {
                inspectRegisteredResultItems(listing).forEach(items::add);
            }
        });
        return Set.copyOf(items);
    }

    public static Optional<InspectedOffer> inspectKnownListing(VillagerTrades.ItemListing listing) {
        Optional<InspectedOffer> declaredOffer = inspectDeclaredListing(listing);
        if (declaredOffer.isPresent()) {
            return declaredOffer;
        }
        return inspectGeneratedOffer(listing, 0L);
    }

    private static Set<Item> inspectRegisteredResultItems(VillagerTrades.ItemListing listing) {
        Optional<InspectedOffer> declaredOffer = inspectDeclaredListing(listing);
        if (declaredOffer.isPresent()) {
            return Set.of(declaredOffer.get().item());
        }

        Set<Item> items = new HashSet<>();
        for (long seed = 0L; seed < GENERATED_LISTING_SAMPLE_COUNT; seed++) {
            inspectGeneratedOffer(listing, seed)
                    .map(InspectedOffer::item)
                    .ifPresent(items::add);
        }
        return items;
    }

    private static Optional<InspectedOffer> inspectDeclaredListing(VillagerTrades.ItemListing listing) {
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
        } catch (ReflectiveOperationException | ClassCastException exception) {
            CommonTrades.LOGGER.debug("Could not inspect Wandering Trader trade listing {}", listing.getClass().getName(), exception);
        }
        return Optional.empty();
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

    private static Optional<InspectedOffer> inspectGeneratedOffer(VillagerTrades.ItemListing listing, long seed) {
        try {
            MerchantOffer offer = listing.getOffer(null, RandomSource.create(seed));
            if (offer == null) {
                return Optional.empty();
            }
            return knownOffer(offer.getResult(), emeraldCost(offer), offer.getMaxUses(), "sampled offer");
        } catch (RuntimeException exception) {
            CommonTrades.LOGGER.debug("Could not sample Wandering Trader trade listing {}", listing.getClass().getName(), exception);
            return Optional.empty();
        }
    }

    private static int emeraldCost(MerchantOffer offer) {
        if (offer.getCostA().is(Items.EMERALD)) {
            return offer.getCostA().getCount();
        }
        if (offer.getCostB().is(Items.EMERALD)) {
            return offer.getCostB().getCount();
        }
        return -1;
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

    public record InspectedOffer(ResourceLocation resultId, Item item, int amount, int emeraldCost, int maxUses, String note) {
    }
}

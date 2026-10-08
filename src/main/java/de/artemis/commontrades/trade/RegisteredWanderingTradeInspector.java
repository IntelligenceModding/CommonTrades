package de.artemis.commontrades.trade;

import de.artemis.commontrades.CommonTrades;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.TradeCost;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.item.trading.TradeSets;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public final class RegisteredWanderingTradeInspector {
    private static final List<WanderingTradeSet> WANDERING_TRADE_SETS = List.of(
            new WanderingTradeSet("Buying", TradeSets.WANDERING_TRADER_BUYING),
            new WanderingTradeSet("Uncommon", TradeSets.WANDERING_TRADER_UNCOMMON),
            new WanderingTradeSet("Common", TradeSets.WANDERING_TRADER_COMMON));

    private RegisteredWanderingTradeInspector() {
    }

    public static Set<Item> registeredResultItems(HolderLookup.Provider registries) {
        return wanderingTrades(registries).stream()
                .map(InspectedTrade::item)
                .collect(Collectors.toUnmodifiableSet());
    }

    public static List<InspectedTrade> wanderingTrades(HolderLookup.Provider registries) {
        if (registries == null) {
            return List.of();
        }

        Optional<? extends HolderLookup.RegistryLookup<TradeSet>> tradeSetRegistry = registries.lookup(Registries.TRADE_SET);
        if (tradeSetRegistry.isEmpty()) {
            return List.of();
        }

        List<InspectedTrade> trades = new ArrayList<>();
        for (WanderingTradeSet wanderingTradeSet : WANDERING_TRADE_SETS) {
            tradeSetRegistry.get().get(wanderingTradeSet.key()).ifPresent(tradeSetHolder -> {
                for (Holder<VillagerTrade> tradeHolder : tradeSetHolder.value().getTrades()) {
                    inspectTrade(wanderingTradeSet.label(), tradeHolder).ifPresent(trades::add);
                }
            });
        }
        return List.copyOf(trades);
    }

    private static Optional<InspectedTrade> inspectTrade(String group, Holder<VillagerTrade> tradeHolder) {
        try {
            VillagerTrade trade = tradeHolder.value();
            ItemStackTemplate result = field(trade, "gives", ItemStackTemplate.class);
            Item item = result.item().value();
            Identifier resultId = result.item()
                    .unwrapKey()
                    .map(ResourceKey::identifier)
                    .orElseGet(() -> BuiltInRegistries.ITEM.getKey(item));
            if (resultId == null || item == Items.AIR) {
                return Optional.empty();
            }

            TradeCost wants = field(trade, "wants", TradeCost.class);
            Optional<TradeCost> additionalWants = optionalField(trade, "additionalWants", TradeCost.class);
            NumberProvider maxUses = field(trade, "maxUses", NumberProvider.class);
            List<LootItemFunction> modifiers = listField(trade, "givenItemModifiers", LootItemFunction.class);
            String note = modifiers.isEmpty() ? "" : "modified result";

            return Optional.of(new InspectedTrade(
                    group,
                    tradeHolder.unwrapKey().map(ResourceKey::identifier),
                    resultId,
                    item,
                    Integer.toString(result.count()),
                    emeraldCost(wants, additionalWants),
                    numberProvider(maxUses),
                    note));
        } catch (ReflectiveOperationException | ClassCastException exception) {
            CommonTrades.LOGGER.debug("Could not inspect Wandering Trader trade {}", tradeHolder.getRegisteredName(), exception);
            return Optional.empty();
        }
    }

    private static String emeraldCost(TradeCost wants, Optional<TradeCost> additionalWants) {
        if (wants.item().value() == Items.EMERALD) {
            return numberProvider(wants.count());
        }
        if (additionalWants.isPresent() && additionalWants.get().item().value() == Items.EMERALD) {
            return numberProvider(additionalWants.get().count());
        }
        return "other";
    }

    private static String numberProvider(NumberProvider numberProvider) {
        if (numberProvider instanceof ConstantValue constantValue) {
            float value = constantValue.value();
            if (value == Math.round(value)) {
                return Integer.toString(Math.round(value));
            }
            return Float.toString(value);
        }
        return "dynamic";
    }

    private static <T> Optional<T> optionalField(Object target, String name, Class<T> type) throws ReflectiveOperationException {
        Optional<?> value = field(target, name, Optional.class);
        if (value.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(type.cast(value.get()));
    }

    private static <T> List<T> listField(Object target, String name, Class<T> type) throws ReflectiveOperationException {
        List<?> values = field(target, name, List.class);
        List<T> castValues = new ArrayList<>(values.size());
        for (Object value : values) {
            castValues.add(type.cast(value));
        }
        return List.copyOf(castValues);
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

    public record InspectedTrade(
            String group,
            Optional<Identifier> tradeId,
            Identifier resultId,
            Item item,
            String amount,
            String emeraldCost,
            String maxUses,
            String note) {
    }

    private record WanderingTradeSet(String label, ResourceKey<TradeSet> key) {
    }
}

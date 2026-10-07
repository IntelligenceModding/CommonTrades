package de.artemis.commontrades.config;

import de.artemis.commontrades.CommonTrades;
import de.artemis.commontrades.trade.TradeCategory;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public final class CommonTradesConfig {
    public static final CommonTradesConfig INSTANCE;
    public static final ModConfigSpec SPEC;

    private final ModConfigSpec.BooleanValue enabled;
    private final ModConfigSpec.IntValue extraTradesPerTrader;

    private final ModConfigSpec.BooleanValue enableSaplings;
    private final ModConfigSpec.BooleanValue enableFlowers;
    private final ModConfigSpec.BooleanValue enableSeeds;
    private final ModConfigSpec.BooleanValue enableMushrooms;
    private final ModConfigSpec.BooleanValue enableSmallPlants;

    private final ModConfigSpec.IntValue saplingEmeraldCost;
    private final ModConfigSpec.IntValue flowerEmeraldCost;
    private final ModConfigSpec.IntValue seedsEmeraldCost;
    private final ModConfigSpec.IntValue mushroomsEmeraldCost;
    private final ModConfigSpec.IntValue smallPlantsEmeraldCost;

    private final ModConfigSpec.ConfigValue<List<? extends String>> itemBlacklist;
    private final ModConfigSpec.ConfigValue<List<? extends String>> modBlacklist;
    private final ModConfigSpec.ConfigValue<List<? extends String>> tagBlacklist;

    static {
        Pair<CommonTradesConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(CommonTradesConfig::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }

    private CommonTradesConfig(ModConfigSpec.Builder builder) {
        builder.translation("commontrades.configuration.general").push("general");
        enabled = builder
                .translation("commontrades.configuration.general.enabled")
                .comment("Server-authoritative. When false, Common Trades does not add Wandering Trader offers.")
                .define("enabled", true);
        extraTradesPerTrader = builder
                .translation("commontrades.configuration.general.extraTradesPerTrader")
                .comment("Server-authoritative. Target number of Common Trades offers per Wandering Trader. Vanilla selects five generic offers, so values above 3 are intentionally not supported.")
                .defineInRange("extraTradesPerTrader", 2, 0, 3);
        builder.pop();

        builder.translation("commontrades.configuration.categories").push("categories");
        enableSaplings = builder.translation("commontrades.configuration.categories.enableSaplings").comment("Server-authoritative. Enable tagged modded sapling trades.").define("enableSaplings", true);
        enableFlowers = builder.translation("commontrades.configuration.categories.enableFlowers").comment("Server-authoritative. Enable tagged modded flower trades.").define("enableFlowers", true);
        enableSeeds = builder.translation("commontrades.configuration.categories.enableSeeds").comment("Server-authoritative. Enable tagged modded seed trades.").define("enableSeeds", true);
        enableMushrooms = builder.translation("commontrades.configuration.categories.enableMushrooms").comment("Server-authoritative. Enable tagged modded mushroom trades.").define("enableMushrooms", true);
        enableSmallPlants = builder.translation("commontrades.configuration.categories.enableSmallPlants").comment("Server-authoritative. Enable tagged modded small plant trades.").define("enableSmallPlants", true);
        builder.pop();

        builder.translation("commontrades.configuration.pricing").push("pricing");
        saplingEmeraldCost = builder.translation("commontrades.configuration.pricing.saplingEmeraldCost").comment("Server-authoritative. Emerald cost for 1 sapling.").defineInRange("saplingEmeraldCost", 5, 1, 32);
        flowerEmeraldCost = builder.translation("commontrades.configuration.pricing.flowerEmeraldCost").comment("Server-authoritative. Emerald cost for 1 flower.").defineInRange("flowerEmeraldCost", 1, 1, 16);
        seedsEmeraldCost = builder.translation("commontrades.configuration.pricing.seedsEmeraldCost").comment("Server-authoritative. Emerald cost for 1-3 seeds.").defineInRange("seedsEmeraldCost", 1, 1, 16);
        mushroomsEmeraldCost = builder.translation("commontrades.configuration.pricing.mushroomsEmeraldCost").comment("Server-authoritative. Emerald cost for 1 mushroom.").defineInRange("mushroomsEmeraldCost", 1, 1, 16);
        smallPlantsEmeraldCost = builder.translation("commontrades.configuration.pricing.smallPlantsEmeraldCost").comment("Server-authoritative. Emerald cost for 1 small plant.").defineInRange("smallPlantsEmeraldCost", 1, 1, 16);
        builder.pop();

        builder.translation("commontrades.configuration.blacklists").push("blacklists");
        itemBlacklist = builder
                .translation("commontrades.configuration.blacklists.itemBlacklist")
                .comment("Server-authoritative. Item ids that Common Trades must never offer. Example: [\"examplemod:rare_seed\"]")
                .defineListAllowEmpty("itemBlacklist", List.of(), () -> "", value -> value instanceof String);
        modBlacklist = builder
                .translation("commontrades.configuration.blacklists.modBlacklist")
                .comment("Server-authoritative. Mod ids/namespaces that Common Trades must never offer.")
                .defineListAllowEmpty("modBlacklist", List.of(), () -> "", value -> value instanceof String);
        tagBlacklist = builder
                .translation("commontrades.configuration.blacklists.tagBlacklist")
                .comment("Server-authoritative. Item tag ids whose contents Common Trades must never offer. A leading # is optional.")
                .defineListAllowEmpty("tagBlacklist", List.of(), () -> "", value -> value instanceof String);
        builder.pop();
    }

    public static boolean enabled() {
        return get(INSTANCE.enabled);
    }

    public static boolean isLoaded() {
        return SPEC.isLoaded();
    }

    public static int extraTradesPerTrader() {
        return get(INSTANCE.extraTradesPerTrader);
    }

    public static boolean isCategoryEnabled(TradeCategory category) {
        return switch (category) {
            case SAPLINGS -> get(INSTANCE.enableSaplings);
            case FLOWERS -> get(INSTANCE.enableFlowers);
            case SEEDS -> get(INSTANCE.enableSeeds);
            case MUSHROOMS -> get(INSTANCE.enableMushrooms);
            case SMALL_PLANTS -> get(INSTANCE.enableSmallPlants);
        };
    }

    public static int emeraldCost(TradeCategory category) {
        return switch (category) {
            case SAPLINGS -> get(INSTANCE.saplingEmeraldCost);
            case FLOWERS -> get(INSTANCE.flowerEmeraldCost);
            case SEEDS -> get(INSTANCE.seedsEmeraldCost);
            case MUSHROOMS -> get(INSTANCE.mushroomsEmeraldCost);
            case SMALL_PLANTS -> get(INSTANCE.smallPlantsEmeraldCost);
        };
    }

    public static Set<ResourceLocation> itemBlacklist() {
        Set<ResourceLocation> blacklist = new HashSet<>();
        for (String entry : get(INSTANCE.itemBlacklist)) {
            ResourceLocation id = ResourceLocation.tryParse(entry);
            if (id == null) {
                CommonTrades.LOGGER.warn("Ignoring malformed Common Trades item blacklist entry '{}'", entry);
                continue;
            }
            blacklist.add(id);
        }
        return blacklist;
    }

    public static Set<String> modBlacklist() {
        Set<String> blacklist = new HashSet<>();
        for (String entry : get(INSTANCE.modBlacklist)) {
            String modId = entry.toLowerCase(Locale.ROOT);
            if (!ResourceLocation.isValidNamespace(modId)) {
                CommonTrades.LOGGER.warn("Ignoring malformed Common Trades mod blacklist entry '{}'", entry);
                continue;
            }
            blacklist.add(modId);
        }
        return blacklist;
    }

    public static List<TagKey<Item>> tagBlacklist() {
        List<TagKey<Item>> blacklist = new ArrayList<>();
        for (String entry : get(INSTANCE.tagBlacklist)) {
            String idText = entry.startsWith("#") ? entry.substring(1) : entry;
            ResourceLocation id = ResourceLocation.tryParse(idText);
            if (id == null) {
                CommonTrades.LOGGER.warn("Ignoring malformed Common Trades tag blacklist entry '{}'", entry);
                continue;
            }
            blacklist.add(TagKey.create(Registries.ITEM, id));
        }
        return List.copyOf(blacklist);
    }

    private static boolean get(ModConfigSpec.BooleanValue value) {
        return SPEC.isLoaded() ? value.getAsBoolean() : value.getDefault();
    }

    private static int get(ModConfigSpec.IntValue value) {
        return SPEC.isLoaded() ? value.getAsInt() : value.getDefault();
    }

    private static List<? extends String> get(ModConfigSpec.ConfigValue<List<? extends String>> value) {
        return SPEC.isLoaded() ? value.get() : value.getDefault();
    }
}

package de.artemis.commontrades.config;

import de.artemis.commontrades.CommonTrades;
import de.artemis.commontrades.trade.TradeCategory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public final class CommonTradesConfig {
    public static final CommonTradesConfig INSTANCE;
    public static final ForgeConfigSpec SPEC;

    private final ForgeConfigSpec.BooleanValue enabled;
    private final ForgeConfigSpec.IntValue extraTradesPerTrader;

    private final ForgeConfigSpec.BooleanValue enableSaplings;
    private final ForgeConfigSpec.BooleanValue enableFlowers;
    private final ForgeConfigSpec.BooleanValue enableSeeds;
    private final ForgeConfigSpec.BooleanValue enableMushrooms;
    private final ForgeConfigSpec.BooleanValue enableSmallPlants;

    private final ForgeConfigSpec.IntValue saplingEmeraldCost;
    private final ForgeConfigSpec.IntValue flowerEmeraldCost;
    private final ForgeConfigSpec.IntValue seedsEmeraldCost;
    private final ForgeConfigSpec.IntValue mushroomsEmeraldCost;
    private final ForgeConfigSpec.IntValue smallPlantsEmeraldCost;

    private final ForgeConfigSpec.ConfigValue<List<? extends String>> itemBlacklist;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> modBlacklist;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> tagBlacklist;

    static {
        Pair<CommonTradesConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(CommonTradesConfig::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }

    private CommonTradesConfig(ForgeConfigSpec.Builder builder) {
        builder.push("general");
        enabled = builder
                .translation("commontrades.configuration.general.enabled")
                .comment("Server-authoritative. When false, Common Trades does not add Wandering Trader offers.")
                .define("enabled", true);
        extraTradesPerTrader = builder
                .translation("commontrades.configuration.general.extraTradesPerTrader")
                .comment("Server-authoritative. Target number of Common Trades offers per Wandering Trader. Vanilla selects five generic offers, so values above 3 are intentionally not supported.")
                .defineInRange("extraTradesPerTrader", 2, 0, 3);
        builder.pop();

        builder.push("categories");
        enableSaplings = builder.translation("commontrades.configuration.categories.enableSaplings").comment("Server-authoritative. Enable tagged modded sapling trades.").define("enableSaplings", true);
        enableFlowers = builder.translation("commontrades.configuration.categories.enableFlowers").comment("Server-authoritative. Enable tagged modded flower trades.").define("enableFlowers", true);
        enableSeeds = builder.translation("commontrades.configuration.categories.enableSeeds").comment("Server-authoritative. Enable tagged modded seed trades.").define("enableSeeds", true);
        enableMushrooms = builder.translation("commontrades.configuration.categories.enableMushrooms").comment("Server-authoritative. Enable tagged modded mushroom trades.").define("enableMushrooms", true);
        enableSmallPlants = builder.translation("commontrades.configuration.categories.enableSmallPlants").comment("Server-authoritative. Enable tagged modded small plant trades.").define("enableSmallPlants", true);
        builder.pop();

        builder.push("pricing");
        saplingEmeraldCost = builder.translation("commontrades.configuration.pricing.saplingEmeraldCost").comment("Server-authoritative. Emerald cost for 1 sapling.").defineInRange("saplingEmeraldCost", 5, 1, 32);
        flowerEmeraldCost = builder.translation("commontrades.configuration.pricing.flowerEmeraldCost").comment("Server-authoritative. Emerald cost for 1 flower.").defineInRange("flowerEmeraldCost", 1, 1, 16);
        seedsEmeraldCost = builder.translation("commontrades.configuration.pricing.seedsEmeraldCost").comment("Server-authoritative. Emerald cost for 1-3 seeds.").defineInRange("seedsEmeraldCost", 1, 1, 16);
        mushroomsEmeraldCost = builder.translation("commontrades.configuration.pricing.mushroomsEmeraldCost").comment("Server-authoritative. Emerald cost for 1 mushroom.").defineInRange("mushroomsEmeraldCost", 1, 1, 16);
        smallPlantsEmeraldCost = builder.translation("commontrades.configuration.pricing.smallPlantsEmeraldCost").comment("Server-authoritative. Emerald cost for 1 small plant.").defineInRange("smallPlantsEmeraldCost", 1, 1, 16);
        builder.pop();

        builder.push("blacklists");
        itemBlacklist = builder
                .translation("commontrades.configuration.blacklists.itemBlacklist")
                .comment("Server-authoritative. Item ids that Common Trades must never offer. Example: [\"examplemod:rare_seed\"]")
                .defineListAllowEmpty(Collections.singletonList("itemBlacklist"), () -> Collections.<String>emptyList(), value -> value instanceof String);
        modBlacklist = builder
                .translation("commontrades.configuration.blacklists.modBlacklist")
                .comment("Server-authoritative. Mod ids/namespaces that Common Trades must never offer.")
                .defineListAllowEmpty(Collections.singletonList("modBlacklist"), () -> Collections.<String>emptyList(), value -> value instanceof String);
        tagBlacklist = builder
                .translation("commontrades.configuration.blacklists.tagBlacklist")
                .comment("Server-authoritative. Item tag ids whose contents Common Trades must never offer. A leading # is optional.")
                .defineListAllowEmpty(Collections.singletonList("tagBlacklist"), () -> Collections.<String>emptyList(), value -> value instanceof String);
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
        switch (category) {
            case SAPLINGS:
                return get(INSTANCE.enableSaplings);
            case FLOWERS:
                return get(INSTANCE.enableFlowers);
            case SEEDS:
                return get(INSTANCE.enableSeeds);
            case MUSHROOMS:
                return get(INSTANCE.enableMushrooms);
            case SMALL_PLANTS:
                return get(INSTANCE.enableSmallPlants);
            default:
                throw new IllegalArgumentException("Unknown trade category " + category);
        }
    }

    public static int emeraldCost(TradeCategory category) {
        switch (category) {
            case SAPLINGS:
                return get(INSTANCE.saplingEmeraldCost);
            case FLOWERS:
                return get(INSTANCE.flowerEmeraldCost);
            case SEEDS:
                return get(INSTANCE.seedsEmeraldCost);
            case MUSHROOMS:
                return get(INSTANCE.mushroomsEmeraldCost);
            case SMALL_PLANTS:
                return get(INSTANCE.smallPlantsEmeraldCost);
            default:
                throw new IllegalArgumentException("Unknown trade category " + category);
        }
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
            if (!isValidNamespace(modId)) {
                CommonTrades.LOGGER.warn("Ignoring malformed Common Trades mod blacklist entry '{}'", entry);
                continue;
            }
            blacklist.add(modId);
        }
        return blacklist;
    }

    public static List<ITag.INamedTag<Item>> tagBlacklist() {
        List<ITag.INamedTag<Item>> blacklist = new ArrayList<>();
        for (String entry : get(INSTANCE.tagBlacklist)) {
            String idText = entry.startsWith("#") ? entry.substring(1) : entry;
            ResourceLocation id = ResourceLocation.tryParse(idText);
            if (id == null) {
                CommonTrades.LOGGER.warn("Ignoring malformed Common Trades tag blacklist entry '{}'", entry);
                continue;
            }
            blacklist.add(ItemTags.createOptional(id));
        }
        return Collections.unmodifiableList(blacklist);
    }

    private static boolean get(ForgeConfigSpec.BooleanValue value) {
        return value.get();
    }

    private static int get(ForgeConfigSpec.IntValue value) {
        return value.get();
    }

    private static List<? extends String> get(ForgeConfigSpec.ConfigValue<List<? extends String>> value) {
        return value.get();
    }

    private static boolean isValidNamespace(String namespace) {
        if (namespace.trim().isEmpty()) {
            return false;
        }
        for (int index = 0; index < namespace.length(); index++) {
            char character = namespace.charAt(index);
            if ((character < 'a' || character > 'z')
                    && (character < '0' || character > '9')
                    && character != '_'
                    && character != '-'
                    && character != '.') {
                return false;
            }
        }
        return true;
    }
}

package de.artemis.commontrades.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.artemis.commontrades.CommonTrades;
import de.artemis.commontrades.trade.TradeCategory;
import de.artemis.commontrades.trade.TradePoolCache;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class CommonTradesConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Values values = new Values();
    private static boolean loaded;

    private CommonTradesConfig() {
    }

    public static void load() {
        Path path = path();
        if (Files.exists(path)) {
            try (Reader reader = Files.newBufferedReader(path)) {
                Values loadedValues = GSON.fromJson(reader, Values.class);
                if (loadedValues != null) {
                    values = loadedValues.normalized();
                    loaded = true;
                    TradePoolCache.markDirty();
                    return;
                }
            } catch (IOException exception) {
                CommonTrades.LOGGER.warn("Could not load Common Trades config at {}", path, exception);
            }
        }
        loaded = true;
        save(path);
        TradePoolCache.markDirty();
    }

    public static boolean enabled() {
        return values.enabled;
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static int extraTradesPerTrader() {
        return values.extraTradesPerTrader;
    }

    public static boolean isCategoryEnabled(TradeCategory category) {
        return switch (category) {
            case SAPLINGS -> values.enableSaplings;
            case FLOWERS -> values.enableFlowers;
            case SEEDS -> values.enableSeeds;
            case MUSHROOMS -> values.enableMushrooms;
            case SMALL_PLANTS -> values.enableSmallPlants;
        };
    }

    public static int emeraldCost(TradeCategory category) {
        return switch (category) {
            case SAPLINGS -> values.saplingEmeraldCost;
            case FLOWERS -> values.flowerEmeraldCost;
            case SEEDS -> values.seedsEmeraldCost;
            case MUSHROOMS -> values.mushroomsEmeraldCost;
            case SMALL_PLANTS -> values.smallPlantsEmeraldCost;
        };
    }

    public static Set<ResourceLocation> itemBlacklist() {
        Set<ResourceLocation> blacklist = new HashSet<>();
        for (String entry : values.itemBlacklist) {
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
        for (String entry : values.modBlacklist) {
            String modId = entry.toLowerCase(Locale.ROOT);
            if (ResourceLocation.tryParse(modId + ":placeholder") == null) {
                CommonTrades.LOGGER.warn("Ignoring malformed Common Trades mod blacklist entry '{}'", entry);
                continue;
            }
            blacklist.add(modId);
        }
        return blacklist;
    }

    public static List<TagKey<Item>> tagBlacklist() {
        List<TagKey<Item>> blacklist = new ArrayList<>();
        for (String entry : values.tagBlacklist) {
            String idText = entry.startsWith("#") ? entry.substring(1) : entry;
            ResourceLocation id = ResourceLocation.tryParse(idText);
            if (id == null) {
                CommonTrades.LOGGER.warn("Ignoring malformed Common Trades tag blacklist entry '{}'", entry);
                continue;
            }
            blacklist.add(TagKey.create(Registry.ITEM_REGISTRY, id));
        }
        return List.copyOf(blacklist);
    }

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve(CommonTrades.MOD_ID + ".json");
    }

    private static void save(Path path) {
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path)) {
                GSON.toJson(values, writer);
            }
        } catch (IOException exception) {
            CommonTrades.LOGGER.warn("Could not save Common Trades config at {}", path, exception);
        }
    }

    private static final class Values {
        boolean enabled = true;
        int extraTradesPerTrader = 2;
        boolean enableSaplings = true;
        boolean enableFlowers = true;
        boolean enableSeeds = true;
        boolean enableMushrooms = true;
        boolean enableSmallPlants = true;
        int saplingEmeraldCost = 5;
        int flowerEmeraldCost = 1;
        int seedsEmeraldCost = 1;
        int mushroomsEmeraldCost = 1;
        int smallPlantsEmeraldCost = 1;
        List<String> itemBlacklist = List.of();
        List<String> modBlacklist = List.of();
        List<String> tagBlacklist = List.of();

        Values normalized() {
            extraTradesPerTrader = clamp(extraTradesPerTrader, 0, 3);
            saplingEmeraldCost = clamp(saplingEmeraldCost, 1, 32);
            flowerEmeraldCost = clamp(flowerEmeraldCost, 1, 16);
            seedsEmeraldCost = clamp(seedsEmeraldCost, 1, 16);
            mushroomsEmeraldCost = clamp(mushroomsEmeraldCost, 1, 16);
            smallPlantsEmeraldCost = clamp(smallPlantsEmeraldCost, 1, 16);
            itemBlacklist = copyOrEmpty(itemBlacklist);
            modBlacklist = copyOrEmpty(modBlacklist);
            tagBlacklist = copyOrEmpty(tagBlacklist);
            return this;
        }

        private static List<String> copyOrEmpty(List<String> entries) {
            return entries == null ? List.of() : List.copyOf(entries);
        }

        private static int clamp(int value, int min, int max) {
            return Math.max(min, Math.min(max, value));
        }
    }
}

package de.artemis.commontrades.trade;

import de.artemis.commontrades.CommonTrades;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class CommonTradeTags {
    public static final TagKey<Item> BLACKLIST = commonTrades("wandering_trader/blacklist");
    public static final TagKey<Item> HIDDEN_FROM_RECIPE_VIEWERS = common("hidden_from_recipe_viewers");

    private CommonTradeTags() {
    }

    static TagKey<Item> commonTrades(String path) {
        return itemTag(CommonTrades.MOD_ID, path);
    }

    static TagKey<Item> minecraft(String path) {
        return itemTag("minecraft", path);
    }

    static TagKey<Item> common(String path) {
        return itemTag("c", path);
    }

    static TagKey<Item> forge(String path) {
        return itemTag("forge", path);
    }

    private static TagKey<Item> itemTag(String namespace, String path) {
        return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(namespace, path));
    }
}

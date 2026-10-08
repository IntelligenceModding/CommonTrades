package de.artemis.commontrades.trade;

import de.artemis.commontrades.CommonTrades;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class CommonTradeTags {
    public static final TagKey<Item> BLACKLIST = commonTrades("wandering_trader/blacklist");

    private CommonTradeTags() {
    }

    static TagKey<Item> commonTrades(String path) {
        return itemTag(CommonTrades.MOD_ID, path);
    }

    static TagKey<Item> common(String path) {
        return itemTag("c", path);
    }

    static TagKey<Item> forge(String path) {
        return itemTag("forge", path);
    }

    private static TagKey<Item> itemTag(String namespace, String path) {
        return TagKey.create(Registries.ITEM, new ResourceLocation(namespace, path));
    }
}

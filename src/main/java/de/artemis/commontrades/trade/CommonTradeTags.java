package de.artemis.commontrades.trade;

import de.artemis.commontrades.CommonTrades;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

public final class CommonTradeTags {
    public static final ITag.INamedTag<Item> BLACKLIST = commonTrades("wandering_trader/blacklist");

    private CommonTradeTags() {
    }

    static ITag.INamedTag<Item> commonTrades(String path) {
        return itemTag(CommonTrades.MOD_ID, path);
    }

    static ITag.INamedTag<Item> minecraft(String path) {
        return itemTag("minecraft", path);
    }

    static ITag.INamedTag<Item> common(String path) {
        return itemTag("c", path);
    }

    static ITag.INamedTag<Item> forge(String path) {
        return itemTag("forge", path);
    }

    private static ITag.INamedTag<Item> itemTag(String namespace, String path) {
        return ItemTags.createOptional(new ResourceLocation(namespace, path));
    }
}

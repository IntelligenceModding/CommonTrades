package de.artemis.commontrades.trade;

import net.minecraft.item.Item;
import net.minecraft.item.MerchantOffer;
import net.minecraft.util.ResourceLocation;

final class SelectedTrade {
    private final Item item;
    private final ResourceLocation itemId;
    private final MerchantOffer offer;

    SelectedTrade(Item item, ResourceLocation itemId, MerchantOffer offer) {
        this.item = item;
        this.itemId = itemId;
        this.offer = offer;
    }

    Item item() {
        return item;
    }

    ResourceLocation itemId() {
        return itemId;
    }

    MerchantOffer offer() {
        return offer;
    }
}

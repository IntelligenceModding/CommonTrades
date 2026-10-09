package de.artemis.commontrades.trade;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.trading.MerchantOffer;

record SelectedTrade(Item item, ResourceLocation itemId, MerchantOffer offer) {
}

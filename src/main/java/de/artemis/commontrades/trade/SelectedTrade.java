package de.artemis.commontrades.trade;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.trading.MerchantOffer;

record SelectedTrade(Item item, Identifier itemId, MerchantOffer offer) {
}

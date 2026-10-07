package de.artemis.commontrades.trade;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public record TradeEntry(TradeCategory category, Item item, ResourceLocation id) {
}

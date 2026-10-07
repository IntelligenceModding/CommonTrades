package de.artemis.commontrades.trade;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public record TradeEntry(TradeCategory category, Item item, Identifier id) {
}

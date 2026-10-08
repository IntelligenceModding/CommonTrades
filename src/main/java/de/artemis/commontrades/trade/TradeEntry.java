package de.artemis.commontrades.trade;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public final class TradeEntry {
    private final TradeCategory category;
    private final Item item;
    private final ResourceLocation id;

    public TradeEntry(TradeCategory category, Item item, ResourceLocation id) {
        this.category = category;
        this.item = item;
        this.id = id;
    }

    public TradeCategory category() {
        return category;
    }

    public Item item() {
        return item;
    }

    public ResourceLocation id() {
        return id;
    }
}

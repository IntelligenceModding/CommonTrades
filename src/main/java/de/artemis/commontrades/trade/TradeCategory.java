package de.artemis.commontrades.trade;

import java.util.List;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/**
 * Declaration order is Common Trades' category priority when an item appears in multiple supported tags:
 * saplings, flowers, mushrooms, seeds, then small plants.
 */
public enum TradeCategory {
    SAPLINGS(
            "saplings",
            List.of(
                    ItemTags.SAPLINGS,
                    CommonTradeTags.common("saplings"),
                    CommonTradeTags.commonTrades("wandering_trader/saplings")),
            1,
            1,
            1,
            8),
    FLOWERS(
            "flowers",
            List.of(
                    BlockItemTags.SMALL_FLOWERS.item(),
                    CommonTradeTags.common("flowers"),
                    CommonTradeTags.commonTrades("wandering_trader/flowers")),
            3,
            1,
            1,
            12),
    MUSHROOMS(
            "mushrooms",
            List.of(
                    CommonTradeTags.common("mushrooms"),
                    CommonTradeTags.commonTrades("wandering_trader/mushrooms")),
            2,
            1,
            1,
            4),
    SEEDS(
            "seeds",
            List.of(
                    ItemTags.VILLAGER_PLANTABLE_SEEDS,
                    CommonTradeTags.common("seeds"),
                    CommonTradeTags.commonTrades("wandering_trader/seeds")),
            3,
            1,
            3,
            12),
    SMALL_PLANTS(
            "small plants",
            List.of(
                    CommonTradeTags.common("small_plants"),
                    CommonTradeTags.common("vines"),
                    CommonTradeTags.common("mosses"),
                    CommonTradeTags.commonTrades("wandering_trader/small_plants")),
            2,
            1,
            1,
            8);

    private final String logName;
    private final List<TagKey<Item>> sourceTags;
    private final int selectionWeight;
    private final int minOutputCount;
    private final int maxOutputCount;
    private final int maxUses;

    TradeCategory(
            String logName,
            List<TagKey<Item>> sourceTags,
            int selectionWeight,
            int minOutputCount,
            int maxOutputCount,
            int maxUses) {
        this.logName = logName;
        this.sourceTags = sourceTags;
        this.selectionWeight = selectionWeight;
        this.minOutputCount = minOutputCount;
        this.maxOutputCount = maxOutputCount;
        this.maxUses = maxUses;
    }

    public String logName() {
        return logName;
    }

    public List<TagKey<Item>> sourceTags() {
        return sourceTags;
    }

    public int selectionWeight() {
        return selectionWeight;
    }

    public int minOutputCount() {
        return minOutputCount;
    }

    public int maxOutputCount() {
        return maxOutputCount;
    }

    public int maxUses() {
        return maxUses;
    }
}

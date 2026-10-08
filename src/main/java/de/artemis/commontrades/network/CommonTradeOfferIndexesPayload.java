package de.artemis.commontrades.network;

import de.artemis.commontrades.CommonTrades;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public final class CommonTradeOfferIndexesPayload {
    public static final ResourceLocation CHANNEL =
            new ResourceLocation(CommonTrades.MOD_ID, "main");

    private static final int MAX_MERCHANT_OFFERS = 64;

    private final int containerId;
    private final int[] offerIndexes;

    public CommonTradeOfferIndexesPayload(int containerId, int[] offerIndexes) {
        this.containerId = containerId;
        this.offerIndexes = offerIndexes;
    }

    public CommonTradeOfferIndexesPayload(PacketBuffer buffer) {
        this(buffer.readVarInt(), buffer.readVarIntArray(MAX_MERCHANT_OFFERS));
    }

    public void write(PacketBuffer buffer) {
        buffer.writeVarInt(this.containerId);
        buffer.writeVarIntArray(this.offerIndexes);
    }

    public int containerId() {
        return containerId;
    }

    public int[] offerIndexes() {
        return offerIndexes;
    }
}

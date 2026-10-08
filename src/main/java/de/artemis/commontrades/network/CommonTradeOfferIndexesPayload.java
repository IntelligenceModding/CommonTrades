package de.artemis.commontrades.network;

import de.artemis.commontrades.CommonTrades;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record CommonTradeOfferIndexesPayload(int containerId, int[] offerIndexes) {
    public static final ResourceLocation CHANNEL =
            new ResourceLocation(CommonTrades.MOD_ID, "main");

    private static final int MAX_MERCHANT_OFFERS = 64;

    public CommonTradeOfferIndexesPayload(FriendlyByteBuf buffer) {
        this(buffer.readVarInt(), buffer.readVarIntArray(MAX_MERCHANT_OFFERS));
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.containerId);
        buffer.writeVarIntArray(this.offerIndexes);
    }
}

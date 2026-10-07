package de.artemis.commontrades.network;

import de.artemis.commontrades.CommonTrades;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record CommonTradeOfferIndexesPayload(int containerId, int[] offerIndexes) implements CustomPacketPayload {
    public static final Type<CommonTradeOfferIndexesPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(CommonTrades.MOD_ID, "common_trade_offer_indexes"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CommonTradeOfferIndexesPayload> STREAM_CODEC =
            CustomPacketPayload.codec(CommonTradeOfferIndexesPayload::write, CommonTradeOfferIndexesPayload::new);

    private static final int MAX_MERCHANT_OFFERS = 64;

    private CommonTradeOfferIndexesPayload(RegistryFriendlyByteBuf buffer) {
        this(buffer.readVarInt(), buffer.readVarIntArray(MAX_MERCHANT_OFFERS));
    }

    private void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeVarInt(this.containerId);
        buffer.writeVarIntArray(this.offerIndexes);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

package de.artemis.commontrades.network;

import de.artemis.commontrades.client.CommonTradeClientOfferMarkers;
import de.artemis.commontrades.trade.CommonTradeOfferManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.trading.MerchantOffers;

public final class CommonTradesNetwork {
    private CommonTradesNetwork() {
    }

    public static void registerPayloads() {
    }

    public static void registerClientPayloads() {
        ClientPlayNetworking.registerGlobalReceiver(
                CommonTradeOfferIndexesPayload.CHANNEL,
                (client, handler, buffer, responseSender) -> {
                    CommonTradeOfferIndexesPayload payload = new CommonTradeOfferIndexesPayload(buffer);
                    client.execute(() -> CommonTradeClientOfferMarkers.acceptSyncedIndexes(
                            payload.containerId(),
                            payload.offerIndexes()));
                });
    }

    public static void sendCommonTradeOfferIndexes(ServerPlayer player, int containerId, MerchantOffers offers) {
        int[] offerIndexes = CommonTradeOfferManager.ownedOfferIndexes(offers);
        if (offerIndexes.length > 0 && ServerPlayNetworking.canSend(player, CommonTradeOfferIndexesPayload.CHANNEL)) {
            FriendlyByteBuf buffer = new FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
            new CommonTradeOfferIndexesPayload(containerId, offerIndexes).write(buffer);
            ServerPlayNetworking.send(player, CommonTradeOfferIndexesPayload.CHANNEL, buffer);
        }
    }
}

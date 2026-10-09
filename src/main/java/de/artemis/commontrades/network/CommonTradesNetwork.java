package de.artemis.commontrades.network;

import de.artemis.commontrades.client.CommonTradeClientOfferMarkers;
import de.artemis.commontrades.trade.CommonTradeOfferManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.trading.MerchantOffers;

public final class CommonTradesNetwork {
    private CommonTradesNetwork() {
    }

    public static void registerPayloads() {
        PayloadTypeRegistry.playS2C().register(
                CommonTradeOfferIndexesPayload.TYPE,
                CommonTradeOfferIndexesPayload.STREAM_CODEC);
    }

    public static void registerClientPayloads() {
        ClientPlayNetworking.registerGlobalReceiver(
                CommonTradeOfferIndexesPayload.TYPE,
                (payload, context) -> CommonTradeClientOfferMarkers.acceptSyncedIndexes(
                        payload.containerId(),
                        payload.offerIndexes()));
    }

    public static void sendCommonTradeOfferIndexes(ServerPlayer player, int containerId, MerchantOffers offers) {
        int[] offerIndexes = CommonTradeOfferManager.ownedOfferIndexes(offers);
        if (offerIndexes.length > 0 && ServerPlayNetworking.canSend(player, CommonTradeOfferIndexesPayload.TYPE)) {
            ServerPlayNetworking.send(player, new CommonTradeOfferIndexesPayload(containerId, offerIndexes));
        }
    }
}

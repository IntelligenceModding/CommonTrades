package de.artemis.commontrades.network;

import de.artemis.commontrades.client.CommonTradeClientOfferMarkers;
import de.artemis.commontrades.trade.CommonTradeOfferManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class CommonTradesNetwork {
    private static final String NETWORK_VERSION = "1";

    private CommonTradesNetwork() {
    }

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar(NETWORK_VERSION)
                .playToClient(
                        CommonTradeOfferIndexesPayload.TYPE,
                        CommonTradeOfferIndexesPayload.STREAM_CODEC,
                        (payload, context) -> CommonTradeClientOfferMarkers.acceptSyncedIndexes(
                                payload.containerId(),
                                payload.offerIndexes()));
    }

    public static void sendCommonTradeOfferIndexes(ServerPlayer player, int containerId, MerchantOffers offers) {
        int[] offerIndexes = CommonTradeOfferManager.ownedOfferIndexes(offers);
        if (offerIndexes.length > 0) {
            PacketDistributor.sendToPlayer(player, new CommonTradeOfferIndexesPayload(containerId, offerIndexes));
        }
    }
}

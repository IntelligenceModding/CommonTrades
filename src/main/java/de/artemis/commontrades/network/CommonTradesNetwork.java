package de.artemis.commontrades.network;

import de.artemis.commontrades.client.CommonTradeClientOfferMarkers;
import de.artemis.commontrades.trade.CommonTradeOfferManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

public final class CommonTradesNetwork {
    private static final int NETWORK_VERSION = 1;
    private static final SimpleChannel CHANNEL = ChannelBuilder
            .named(CommonTradeOfferIndexesPayload.CHANNEL)
            .networkProtocolVersion(NETWORK_VERSION)
            .acceptedVersions(Channel.VersionTest.exact(NETWORK_VERSION))
            .simpleChannel();

    private CommonTradesNetwork() {
    }

    public static void registerPayloads() {
        int packetId = 0;
        CHANNEL.messageBuilder(CommonTradeOfferIndexesPayload.class, packetId, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(CommonTradeOfferIndexesPayload::write)
                .decoder(CommonTradeOfferIndexesPayload::new)
                .consumerMainThread(CommonTradesNetwork::handleCommonTradeOfferIndexes)
                .add();
    }

    public static void sendCommonTradeOfferIndexes(ServerPlayer player, int containerId, MerchantOffers offers) {
        int[] offerIndexes = CommonTradeOfferManager.ownedOfferIndexes(offers);
        if (offerIndexes.length > 0) {
            CHANNEL.send(new CommonTradeOfferIndexesPayload(containerId, offerIndexes), PacketDistributor.PLAYER.with(player));
        }
    }

    private static void handleCommonTradeOfferIndexes(
            CommonTradeOfferIndexesPayload payload,
            CustomPayloadEvent.Context context) {
        DistExecutor.unsafeRunWhenOn(
                Dist.CLIENT,
                () -> () -> CommonTradeClientOfferMarkers.acceptSyncedIndexes(
                        payload.containerId(),
                        payload.offerIndexes()));
        context.setPacketHandled(true);
    }
}

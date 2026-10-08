package de.artemis.commontrades.network;

import de.artemis.commontrades.client.CommonTradeClientOfferMarkers;
import de.artemis.commontrades.trade.CommonTradeOfferManager;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class CommonTradesNetwork {
    private static final String NETWORK_VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(CommonTradeOfferIndexesPayload.CHANNEL)
            .networkProtocolVersion(() -> NETWORK_VERSION)
            .clientAcceptedVersions(NETWORK_VERSION::equals)
            .serverAcceptedVersions(NETWORK_VERSION::equals)
            .simpleChannel();

    private CommonTradesNetwork() {
    }

    public static void registerPayloads() {
        int packetId = 0;
        CHANNEL.registerMessage(
                packetId,
                CommonTradeOfferIndexesPayload.class,
                CommonTradeOfferIndexesPayload::write,
                CommonTradeOfferIndexesPayload::new,
                CommonTradesNetwork::handleCommonTradeOfferIndexes,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void sendCommonTradeOfferIndexes(ServerPlayer player, int containerId, MerchantOffers offers) {
        int[] offerIndexes = CommonTradeOfferManager.ownedOfferIndexes(offers);
        if (offerIndexes.length > 0) {
            CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new CommonTradeOfferIndexesPayload(containerId, offerIndexes));
        }
    }

    private static void handleCommonTradeOfferIndexes(
            CommonTradeOfferIndexesPayload payload,
            Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(
                Dist.CLIENT,
                () -> () -> CommonTradeClientOfferMarkers.acceptSyncedIndexes(
                        payload.containerId(),
                        payload.offerIndexes())));
        context.setPacketHandled(true);
    }
}

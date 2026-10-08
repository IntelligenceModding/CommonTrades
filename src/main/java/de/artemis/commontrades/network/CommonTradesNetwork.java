package de.artemis.commontrades.network;

import de.artemis.commontrades.client.CommonTradeClientOfferMarkers;
import de.artemis.commontrades.trade.CommonTradeOfferManager;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.MerchantOffers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public final class CommonTradesNetwork {
    private static final String NETWORK_VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            CommonTradeOfferIndexesPayload.CHANNEL,
            () -> NETWORK_VERSION,
            NETWORK_VERSION::equals,
            NETWORK_VERSION::equals);

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

    public static void sendCommonTradeOfferIndexes(ServerPlayerEntity player, int containerId, MerchantOffers offers) {
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

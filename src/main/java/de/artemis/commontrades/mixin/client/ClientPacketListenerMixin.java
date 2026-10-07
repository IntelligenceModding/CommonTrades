package de.artemis.commontrades.mixin.client;

import de.artemis.commontrades.client.CommonTradeClientOfferMarkers;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundMerchantOffersPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
abstract class ClientPacketListenerMixin {
    @Inject(method = "handleMerchantOffers", at = @At("HEAD"))
    private void commontrades$clearCommonTradeOfferIndexes(ClientboundMerchantOffersPacket packet, CallbackInfo callbackInfo) {
        CommonTradeClientOfferMarkers.clearSyncedIndexes(packet.getContainerId());
    }
}

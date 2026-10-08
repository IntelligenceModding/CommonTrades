package de.artemis.commontrades.mixin.client;

import de.artemis.commontrades.client.CommonTradeClientOfferMarkers;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.play.server.SMerchantOffersPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetHandler.class)
abstract class ClientPacketListenerMixin {
    @Inject(method = {"handleMerchantOffers", "func_217273_a"}, at = @At("HEAD"), remap = false)
    private void commontrades$clearCommonTradeOfferIndexes(SMerchantOffersPacket packet, CallbackInfo callbackInfo) {
        CommonTradeClientOfferMarkers.clearSyncedIndexes(packet.getContainerId());
    }
}

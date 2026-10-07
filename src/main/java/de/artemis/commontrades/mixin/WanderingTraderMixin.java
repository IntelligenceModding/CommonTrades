package de.artemis.commontrades.mixin;

import de.artemis.commontrades.trade.CommonTradeOfferManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WanderingTrader.class, priority = 500)
abstract class WanderingTraderMixin {
    @Inject(method = "updateTrades", at = @At("RETURN"))
    private void commontrades$finalizeGeneratedOffers(ServerLevel level, CallbackInfo callbackInfo) {
        // NeoForge exposes registration-time trade events, but no post-generation event with final MerchantOffers.
        // This targeted hook lets Common Trades yield to dynamically generated vanilla/modded offers without
        // overwriting or inspecting third-party ItemListing internals.
        CommonTradeOfferManager.finalizeOffers((WanderingTrader) (Object) this);
    }
}

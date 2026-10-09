package de.artemis.commontrades.mixin;

import de.artemis.commontrades.trade.CommonTradeOfferManager;
import net.minecraft.world.entity.npc.WanderingTrader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WanderingTrader.class, priority = 500)
abstract class WanderingTraderMixin {
    @Inject(method = "updateTrades", at = @At("RETURN"))
    private void commontrades$finalizeGeneratedOffers(CallbackInfo callbackInfo) {
        // This targeted hook lets Common Trades yield to dynamically generated vanilla/modded offers
        // without overwriting or inspecting third-party trade internals.
        CommonTradeOfferManager.finalizeOffers((WanderingTrader) (Object) this);
    }
}

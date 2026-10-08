package de.artemis.commontrades.mixin;

import de.artemis.commontrades.trade.CommonTradeOfferManager;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WanderingTraderEntity.class, priority = 500)
abstract class WanderingTraderMixin {
    @Inject(method = {"updateTrades", "func_213712_ef"}, at = @At("RETURN"), remap = false)
    private void commontrades$finalizeGeneratedOffers(CallbackInfo callbackInfo) {
        // Forge exposes registration-time trade events, but no post-generation event with final MerchantOffers.
        // This targeted hook lets Common Trades yield to dynamically generated vanilla/modded offers without
        // overwriting or inspecting third-party ItemListing internals.
        CommonTradeOfferManager.finalizeOffers((WanderingTraderEntity) (Object) this);
    }
}

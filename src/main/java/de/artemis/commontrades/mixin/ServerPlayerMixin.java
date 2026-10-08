package de.artemis.commontrades.mixin;

import de.artemis.commontrades.network.CommonTradesNetwork;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
abstract class ServerPlayerMixin {
    @Inject(method = {"sendMerchantOffers", "func_213818_a"}, at = @At("TAIL"), remap = false)
    private void commontrades$syncCommonTradeOfferIndexes(
            int containerId,
            MerchantOffers offers,
            int level,
            int xp,
            boolean showProgress,
            boolean canRestock,
            CallbackInfo callbackInfo) {
        CommonTradesNetwork.sendCommonTradeOfferIndexes((ServerPlayerEntity) (Object) this, containerId, offers);
    }
}

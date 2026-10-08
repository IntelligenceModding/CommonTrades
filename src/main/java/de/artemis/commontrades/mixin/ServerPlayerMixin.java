package de.artemis.commontrades.mixin;

import de.artemis.commontrades.network.CommonTradesNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
abstract class ServerPlayerMixin {
    @Inject(method = {"sendMerchantOffers", "m_7662_"}, at = @At("TAIL"), remap = false)
    private void commontrades$syncCommonTradeOfferIndexes(
            int containerId,
            MerchantOffers offers,
            int level,
            int xp,
            boolean showProgress,
            boolean canRestock,
            CallbackInfo callbackInfo) {
        CommonTradesNetwork.sendCommonTradeOfferIndexes((ServerPlayer) (Object) this, containerId, offers);
    }
}

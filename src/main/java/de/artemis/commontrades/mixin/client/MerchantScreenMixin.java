package de.artemis.commontrades.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.artemis.commontrades.client.CommonTradeClientOfferMarkers;
import de.artemis.commontrades.config.CommonTradesClientConfig;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.inventory.MerchantScreen;
import net.minecraft.inventory.container.MerchantContainer;
import net.minecraft.item.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantScreen.class)
abstract class MerchantScreenMixin {
    private static final int TRADE_BUTTON_X = 5;
    private static final int TRADE_BUTTON_WIDTH = 88;
    private static final int TRADE_BUTTON_HEIGHT = 20;

    @Inject(method = {"renderButtonArrows", "func_238842_a_"}, at = @At("RETURN"), remap = false)
    private void commontrades$renderOfferMarker(
            MatrixStack matrixStack,
            MerchantOffer merchantOffer,
            int posX,
            int posY,
            CallbackInfo callbackInfo) {
        MerchantContainer menu = ((MerchantScreen) (Object) this).getMenu();
        if (CommonTradesClientConfig.visualIndicators() && CommonTradeClientOfferMarkers.isMarked(menu, merchantOffer)) {
            int buttonX = posX + TRADE_BUTTON_X;
            int buttonY = posY - 1;
            int left = buttonX + 1;
            int top = buttonY + 1;
            int right = buttonX + TRADE_BUTTON_WIDTH - 1;
            int bottom = buttonY + TRADE_BUTTON_HEIGHT - 1;
            int markerColor = CommonTradesClientConfig.outlineColor();
            AbstractGui.fill(matrixStack, left, top, right, top + 1, markerColor);
            AbstractGui.fill(matrixStack, left, bottom - 1, right, bottom, markerColor);
            AbstractGui.fill(matrixStack, left, top, left + 1, bottom, markerColor);
            AbstractGui.fill(matrixStack, right - 1, top, right, bottom, markerColor);
        }
    }
}

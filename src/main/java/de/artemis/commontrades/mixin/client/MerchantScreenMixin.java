package de.artemis.commontrades.mixin.client;

import de.artemis.commontrades.client.CommonTradeClientOfferMarkers;
import de.artemis.commontrades.config.CommonTradesClientConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantScreen.class)
abstract class MerchantScreenMixin {
    private static final int TRADE_BUTTON_X = 5;
    private static final int TRADE_BUTTON_WIDTH = 88;
    private static final int TRADE_BUTTON_HEIGHT = 20;

    @Inject(method = {"renderButtonArrows", "m_280526_"}, at = @At("RETURN"), remap = false)
    private void commontrades$renderOfferMarker(
            GuiGraphics guiGraphics,
            MerchantOffer merchantOffer,
            int posX,
            int posY,
            CallbackInfo callbackInfo) {
        MerchantMenu menu = ((MerchantScreen) (Object) this).getMenu();
        if (CommonTradesClientConfig.visualIndicators() && CommonTradeClientOfferMarkers.isMarked(menu, merchantOffer)) {
            int buttonX = posX + TRADE_BUTTON_X;
            int buttonY = posY - 1;
            int left = buttonX + 1;
            int top = buttonY + 1;
            int right = buttonX + TRADE_BUTTON_WIDTH - 1;
            int bottom = buttonY + TRADE_BUTTON_HEIGHT - 1;
            int markerColor = CommonTradesClientConfig.outlineColor();
            guiGraphics.fill(left, top, right, top + 1, markerColor);
            guiGraphics.fill(left, bottom - 1, right, bottom, markerColor);
            guiGraphics.fill(left, top, left + 1, bottom, markerColor);
            guiGraphics.fill(right - 1, top, right, bottom, markerColor);
        }
    }
}

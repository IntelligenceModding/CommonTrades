package de.artemis.commontrades.mixin.client;

import de.artemis.commontrades.client.CommonTradeClientOfferMarkers;
import de.artemis.commontrades.config.CommonTradesClientConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.world.inventory.MerchantMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantScreen.class)
abstract class MerchantScreenMixin {
    private static final int TRADE_BUTTON_X = 5;
    private static final int TRADE_BUTTON_WIDTH = 88;
    private static final int TRADE_BUTTON_HEIGHT = 20;
    private static final int TRADE_BUTTON_COUNT = 7;
    private static final int TRADE_BUTTON_TOP_OFFSET = 18;
    private static final int TRADE_BUTTON_SPACING = 20;

    @Shadow
    private int scrollOff;

    @Inject(method = "renderContents", at = @At("RETURN"))
    private void commontrades$renderOfferMarkers(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick,
            CallbackInfo callbackInfo) {
        MerchantMenu menu = (MerchantMenu) ((AbstractContainerScreenAccessor) this).commontrades$getMenu();
        if (!CommonTradesClientConfig.visualIndicators() || menu.getOffers().isEmpty()) {
            return;
        }

        int markerColor = CommonTradesClientConfig.outlineColor();
        int firstVisibleOffer = menu.getOffers().size() > TRADE_BUTTON_COUNT ? this.scrollOff : 0;
        int lastVisibleOffer = Math.min(firstVisibleOffer + TRADE_BUTTON_COUNT, menu.getOffers().size());
        AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) this;
        int screenX = accessor.commontrades$getLeftPos();
        int screenY = accessor.commontrades$getTopPos();

        for (int offerIndex = firstVisibleOffer; offerIndex < lastVisibleOffer; offerIndex++) {
            if (CommonTradeClientOfferMarkers.isMarked(menu, offerIndex)) {
                int row = offerIndex - firstVisibleOffer;
                int buttonX = screenX + TRADE_BUTTON_X;
                int buttonY = screenY + TRADE_BUTTON_TOP_OFFSET + row * TRADE_BUTTON_SPACING;
                renderOutline(guiGraphics, buttonX, buttonY, markerColor);
            }
        }
    }

    private static void renderOutline(GuiGraphics guiGraphics, int x, int y, int color) {
        guiGraphics.renderOutline(x + 1, y + 1, TRADE_BUTTON_WIDTH - 2, TRADE_BUTTON_HEIGHT - 2, color);
    }
}

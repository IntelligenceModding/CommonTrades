package de.artemis.commontrades.mixin.client;

import de.artemis.commontrades.client.CommonTradeClientOfferMarkers;
import de.artemis.commontrades.config.CommonTradesClientConfig;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.gui.screens.inventory.MerchantScreen$TradeOfferButton")
abstract class MerchantTradeOfferButtonMixin {
    @Shadow
    @Final
    int index;

    @Shadow
    @Final
    private MerchantScreen this$0;

    @Inject(method = "extractToolTip", at = @At("HEAD"), cancellable = true)
    private void commontrades$renderMarkedResultTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, CallbackInfo callbackInfo) {
        if (!CommonTradesClientConfig.visualIndicators()) {
            return;
        }

        Button button = (Button) (Object) this;
        if (!button.isHovered() || mouseX <= button.getX() + 65) {
            return;
        }

        MerchantMenu menu = (MerchantMenu) ((AbstractContainerScreenAccessor) this.this$0).commontrades$getMenu();
        int offerIndex = this.index + ((MerchantScreenAccessor) this.this$0).commontrades$getScrollOff();
        if (menu.getOffers().size() <= offerIndex || !CommonTradeClientOfferMarkers.isMarked(menu, offerIndex)) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        ItemStack result = menu.getOffers().get(offerIndex).getResult();
        List<Component> tooltip = new ArrayList<>(Screen.getTooltipFromItem(minecraft, result));
        tooltip.add(Component.translatable("commontrades.tooltip.added_by_common_trades").withStyle(ChatFormatting.GRAY));
        guiGraphics.setTooltipForNextFrame(minecraft.font, tooltip, result.getTooltipImage(), mouseX, mouseY, null);
        callbackInfo.cancel();
    }
}

package de.artemis.commontrades.mixin.client;

import de.artemis.commontrades.client.CommonTradeClientOfferMarkers;
import de.artemis.commontrades.config.CommonTradesClientConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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

    @Inject(method = "renderToolTip", at = @At("HEAD"), cancellable = true)
    private void commontrades$renderMarkedResultTooltip(PoseStack poseStack, int mouseX, int mouseY, CallbackInfo callbackInfo) {
        if (!CommonTradesClientConfig.visualIndicators()) {
            return;
        }

        Button button = (Button) (Object) this;
        if (!button.isMouseOver(mouseX, mouseY)) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (!(minecraft.screen instanceof MerchantScreen merchantScreen)) {
            return;
        }

        MerchantMenu menu = (MerchantMenu) ((AbstractContainerScreenAccessor) merchantScreen).commontrades$getMenu();
        int offerIndex = this.index + ((MerchantScreenAccessor) merchantScreen).commontrades$getScrollOff();
        if (menu.getOffers().size() <= offerIndex || !CommonTradeClientOfferMarkers.isMarked(menu, offerIndex)) {
            return;
        }

        ItemStack result = menu.getOffers().get(offerIndex).getResult();
        List<Component> tooltip = new ArrayList<>(merchantScreen.getTooltipFromItem(result));
        tooltip.add(new TranslatableComponent("commontrades.tooltip.added_by_common_trades").withStyle(ChatFormatting.GRAY));
        merchantScreen.renderComponentTooltip(poseStack, tooltip, mouseX, mouseY);
        callbackInfo.cancel();
    }
}

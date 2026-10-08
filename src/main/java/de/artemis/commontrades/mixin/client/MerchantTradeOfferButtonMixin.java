package de.artemis.commontrades.mixin.client;

import de.artemis.commontrades.client.CommonTradeClientOfferMarkers;
import de.artemis.commontrades.config.CommonTradesClientConfig;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.gui.screens.inventory.MerchantScreen$TradeOfferButton")
abstract class MerchantTradeOfferButtonMixin {
    @Inject(method = {"renderToolTip", "m_7428_"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void commontrades$renderMarkedResultTooltip(PoseStack poseStack, int mouseX, int mouseY, CallbackInfo callbackInfo) {
        if (!CommonTradesClientConfig.visualIndicators()) {
            return;
        }

        Button button = (Button) (Object) this;
        if (!button.isMouseOver(mouseX, mouseY)) {
            return;
        }

        MerchantScreen screen = ownerScreen();
        if (screen == null) {
            return;
        }

        MerchantMenu menu = screen.getMenu();
        int offerIndex = tradeButtonIndex() + scrollOffset(screen);
        if (menu.getOffers().size() <= offerIndex || !CommonTradeClientOfferMarkers.isMarked(menu, offerIndex)) {
            return;
        }

        ItemStack result = menu.getOffers().get(offerIndex).getResult();
        List<Component> tooltip = new ArrayList<>(screen.getTooltipFromItem(result));
        tooltip.add(Component.translatable("commontrades.tooltip.added_by_common_trades").withStyle(ChatFormatting.GRAY));
        screen.renderComponentTooltip(poseStack, tooltip, mouseX, mouseY);
        callbackInfo.cancel();
    }

    private MerchantScreen ownerScreen() {
        Object value = fieldValue("this$0", "f_99202_");
        return value instanceof MerchantScreen screen ? screen : null;
    }

    private int tradeButtonIndex() {
        Object value = fieldValue("index", "f_99201_");
        return value instanceof Integer index ? index : 0;
    }

    private static int scrollOffset(MerchantScreen screen) {
        Object value = fieldValue(screen, "scrollOff", "f_99119_");
        return value instanceof Integer scrollOff ? scrollOff : 0;
    }

    private Object fieldValue(String namedField, String obfuscatedField) {
        return fieldValue(this, namedField, obfuscatedField);
    }

    private static Object fieldValue(Object target, String namedField, String obfuscatedField) {
        Field field = findField(target.getClass(), namedField, obfuscatedField);
        if (field == null) {
            return null;
        }
        try {
            field.setAccessible(true);
            return field.get(target);
        } catch (IllegalAccessException exception) {
            return null;
        }
    }

    private static Field findField(Class<?> type, String namedField, String obfuscatedField) {
        Class<?> current = type;
        while (current != null) {
            for (String fieldName : List.of(namedField, obfuscatedField)) {
                try {
                    return current.getDeclaredField(fieldName);
                } catch (NoSuchFieldException exception) {
                    // Try the next known name.
                }
            }
            current = current.getSuperclass();
        }
        return null;
    }
}

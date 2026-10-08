package de.artemis.commontrades.mixin.client;

import de.artemis.commontrades.client.CommonTradeClientOfferMarkers;
import de.artemis.commontrades.config.CommonTradesClientConfig;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.MerchantScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.inventory.container.MerchantContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.gui.screen.inventory.MerchantScreen$TradeButton")
abstract class MerchantTradeOfferButtonMixin {
    @Inject(method = {"renderToolTip", "func_230443_a_"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void commontrades$renderMarkedResultTooltip(MatrixStack matrixStack, int mouseX, int mouseY, CallbackInfo callbackInfo) {
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

        MerchantContainer menu = screen.getMenu();
        int offerIndex = tradeButtonIndex() + scrollOffset(screen);
        if (menu.getOffers().size() <= offerIndex || !CommonTradeClientOfferMarkers.isMarked(menu, offerIndex)) {
            return;
        }

        ItemStack result = menu.getOffers().get(offerIndex).getResult();
        List<ITextComponent> tooltip = new ArrayList<>(screen.getTooltipFromItem(result));
        tooltip.add(new TranslationTextComponent("commontrades.tooltip.added_by_common_trades").withStyle(TextFormatting.GRAY));
        screen.renderComponentTooltip(matrixStack, tooltip, mouseX, mouseY);
        callbackInfo.cancel();
    }

    private MerchantScreen ownerScreen() {
        Object value = fieldValue("this$0", "field_212939_b");
        return value instanceof MerchantScreen ? (MerchantScreen) value : null;
    }

    private int tradeButtonIndex() {
        Object value = fieldValue("index", "field_212938_a");
        return value instanceof Integer ? (Integer) value : 0;
    }

    private static int scrollOffset(MerchantScreen screen) {
        Object value = fieldValue(screen, "scrollOff", "field_214139_n");
        return value instanceof Integer ? (Integer) value : 0;
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
            for (String fieldName : Arrays.asList(namedField, obfuscatedField)) {
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

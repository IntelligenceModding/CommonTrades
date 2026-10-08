package de.artemis.commontrades.client;

import com.mojang.blaze3d.vertex.PoseStack;
import de.artemis.commontrades.config.CommonTradesClientConfig;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;

final class CommonTradesConfigScreen extends Screen {
    private static final int ROW_WIDTH = 310;
    private static final int LABEL_WIDTH = 130;
    private static final int CONTROL_WIDTH = 170;
    private static final int ROW_HEIGHT = 24;
    private static final int TEXT_COLOR = 0xE0E0E0;
    private static final int ERROR_COLOR = 0xFF5555;
    private static final Component TITLE = new TranslatableComponent("commontrades.configuration.title", new TranslatableComponent("mod.commontrades"));
    private static final Component VISUAL_MARKERS = new TranslatableComponent("commontrades.configuration.visualMarkers");
    private static final Component INVALID_COLOR = new TranslatableComponent("commontrades.configuration.visualMarkers.outlineColor.invalid");
    private static final Component SAVE = new TranslatableComponent("commontrades.configuration.save");
    private static final Component CANCEL = new TranslatableComponent("gui.cancel");

    private final Screen parent;
    private boolean visualIndicators;
    private int outlineOpacity;
    private String outlineColor;
    private EditBox outlineColorBox;
    private Button saveButton;

    CommonTradesConfigScreen(Screen parent) {
        super(TITLE);
        this.parent = parent;
        this.visualIndicators = CommonTradesClientConfig.visualIndicators();
        this.outlineOpacity = CommonTradesClientConfig.outlineOpacity();
        this.outlineColor = CommonTradesClientConfig.outlineColorText();
    }

    @Override
    protected void init() {
        int left = Math.max(8, (this.width - ROW_WIDTH) / 2);
        int top = Math.max(42, this.height / 2 - 72);
        int controlX = left + LABEL_WIDTH + 10;

        addRenderableWidget(CycleButton.onOffBuilder(this.visualIndicators)
                .create(controlX, top, CONTROL_WIDTH, 20, new TranslatableComponent("commontrades.configuration.visualMarkers.visualIndicators"), (button, value) -> this.visualIndicators = value));

        addRenderableWidget(new OpacitySlider(controlX, top + ROW_HEIGHT, CONTROL_WIDTH, 20, this.outlineOpacity));

        this.outlineColorBox = new EditBox(this.font, controlX, top + ROW_HEIGHT * 2, CONTROL_WIDTH, 20, new TranslatableComponent("commontrades.configuration.visualMarkers.outlineColor"));
        this.outlineColorBox.setMaxLength(8);
        this.outlineColorBox.setValue(this.outlineColor);
        this.outlineColorBox.setResponder(value -> {
            this.outlineColor = value;
            updateSaveButton();
        });
        addRenderableWidget(this.outlineColorBox);

        int buttonY = Math.min(this.height - 28, top + ROW_HEIGHT * 4 + 12);
        this.saveButton = addRenderableWidget(new Button(this.width / 2 - 155, buttonY, 150, 20, SAVE, button -> saveAndClose()));
        addRenderableWidget(new Button(this.width / 2 + 5, buttonY, 150, 20, CANCEL, button -> onClose()));
        updateSaveButton();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        renderBackground(poseStack);
        int left = Math.max(8, (this.width - ROW_WIDTH) / 2);
        int top = Math.max(42, this.height / 2 - 72);

        drawCenteredString(poseStack, this.font, this.title, this.width / 2, 16, 0xFFFFFF);
        drawCenteredString(poseStack, this.font, VISUAL_MARKERS, this.width / 2, top - 18, 0xFFFFFF);
        drawLabel(poseStack, "visualIndicators", left, top + 6);
        drawLabel(poseStack, "outlineOpacity", left, top + ROW_HEIGHT + 6);
        drawLabel(poseStack, "outlineColor", left, top + ROW_HEIGHT * 2 + 6);
        super.render(poseStack, mouseX, mouseY, partialTick);

        if (!CommonTradesClientConfig.isColorCode(this.outlineColor)) {
            drawString(poseStack, this.font, INVALID_COLOR, left, top + ROW_HEIGHT * 3 + 8, ERROR_COLOR);
        }
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parent);
        }
    }

    private void drawLabel(PoseStack poseStack, String key, int x, int y) {
        drawString(poseStack, this.font, new TranslatableComponent("commontrades.configuration.visualMarkers." + key), x, y, TEXT_COLOR);
    }

    private void updateSaveButton() {
        if (this.saveButton != null) {
            this.saveButton.active = CommonTradesClientConfig.isColorCode(this.outlineColor);
        }
    }

    private void saveAndClose() {
        CommonTradesClientConfig.saveVisualMarkerSettings(this.visualIndicators, this.outlineOpacity, this.outlineColor);
        onClose();
    }

    private final class OpacitySlider extends AbstractSliderButton {
        private OpacitySlider(int x, int y, int width, int height, int initialValue) {
            super(x, y, width, height, TextComponent.EMPTY, Mth.clamp(initialValue, 0, 100) / 100.0);
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(new TranslatableComponent(
                    "options.generic_value",
                    new TranslatableComponent("commontrades.configuration.visualMarkers.outlineOpacity"),
                    new TextComponent(Integer.toString(CommonTradesConfigScreen.this.outlineOpacity) + "%")));
        }

        @Override
        protected void applyValue() {
            CommonTradesConfigScreen.this.outlineOpacity = Mth.clamp((int) Math.round(this.value * 100.0), 0, 100);
            updateMessage();
        }
    }
}

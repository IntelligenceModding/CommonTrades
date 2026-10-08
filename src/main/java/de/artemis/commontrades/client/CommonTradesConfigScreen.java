package de.artemis.commontrades.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.artemis.commontrades.config.CommonTradesClientConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

final class CommonTradesConfigScreen extends Screen {
    private static final int ROW_WIDTH = 310;
    private static final int LABEL_WIDTH = 130;
    private static final int CONTROL_WIDTH = 170;
    private static final int ROW_HEIGHT = 24;
    private static final int TEXT_COLOR = 0xE0E0E0;
    private static final int ERROR_COLOR = 0xFF5555;
    private static final ITextComponent TITLE = new TranslationTextComponent("commontrades.configuration.title", new TranslationTextComponent("mod.commontrades"));
    private static final ITextComponent VISUAL_MARKERS = new TranslationTextComponent("commontrades.configuration.visualMarkers");
    private static final ITextComponent INVALID_COLOR = new TranslationTextComponent("commontrades.configuration.visualMarkers.outlineColor.invalid");
    private static final ITextComponent SAVE = new TranslationTextComponent("commontrades.configuration.save");
    private static final ITextComponent CANCEL = new TranslationTextComponent("gui.cancel");

    private final Screen parent;
    private boolean visualIndicators;
    private int outlineOpacity;
    private String outlineColor;
    private TextFieldWidget outlineColorBox;
    private Button visualIndicatorsButton;
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

        this.visualIndicatorsButton = addButton(new Button(
                controlX,
                top,
                CONTROL_WIDTH,
                20,
                visualIndicatorsMessage(),
                button -> {
                    this.visualIndicators = !this.visualIndicators;
                    this.visualIndicatorsButton.setMessage(visualIndicatorsMessage());
                }));

        addButton(new OpacitySlider(controlX, top + ROW_HEIGHT, CONTROL_WIDTH, 20, this.outlineOpacity));

        this.outlineColorBox = new TextFieldWidget(this.font, controlX, top + ROW_HEIGHT * 2, CONTROL_WIDTH, 20, new TranslationTextComponent("commontrades.configuration.visualMarkers.outlineColor"));
        this.outlineColorBox.setMaxLength(8);
        this.outlineColorBox.setValue(this.outlineColor);
        this.outlineColorBox.setResponder(value -> {
            this.outlineColor = value;
            updateSaveButton();
        });
        addWidget(this.outlineColorBox);

        int buttonY = Math.min(this.height - 28, top + ROW_HEIGHT * 4 + 12);
        this.saveButton = addButton(new Button(this.width / 2 - 155, buttonY, 150, 20, SAVE, button -> saveAndClose()));
        addButton(new Button(this.width / 2 + 5, buttonY, 150, 20, CANCEL, button -> onClose()));
        updateSaveButton();
    }

    @Override
    public void tick() {
        if (this.outlineColorBox != null) {
            this.outlineColorBox.tick();
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTick) {
        renderBackground(matrixStack);
        int left = Math.max(8, (this.width - ROW_WIDTH) / 2);
        int top = Math.max(42, this.height / 2 - 72);

        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 16, 0xFFFFFF);
        drawCenteredString(matrixStack, this.font, VISUAL_MARKERS, this.width / 2, top - 18, 0xFFFFFF);
        drawLabel(matrixStack, "visualIndicators", left, top + 6);
        drawLabel(matrixStack, "outlineOpacity", left, top + ROW_HEIGHT + 6);
        drawLabel(matrixStack, "outlineColor", left, top + ROW_HEIGHT * 2 + 6);
        super.render(matrixStack, mouseX, mouseY, partialTick);
        this.outlineColorBox.render(matrixStack, mouseX, mouseY, partialTick);

        if (!CommonTradesClientConfig.isColorCode(this.outlineColor)) {
            drawString(matrixStack, this.font, INVALID_COLOR, left, top + ROW_HEIGHT * 3 + 8, ERROR_COLOR);
        }
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parent);
        }
    }

    private ITextComponent visualIndicatorsMessage() {
        return new TranslationTextComponent(
                "options.generic_value",
                new TranslationTextComponent("commontrades.configuration.visualMarkers.visualIndicators"),
                new TranslationTextComponent(this.visualIndicators ? "options.on" : "options.off"));
    }

    private void drawLabel(MatrixStack matrixStack, String key, int x, int y) {
        drawString(matrixStack, this.font, new TranslationTextComponent("commontrades.configuration.visualMarkers." + key), x, y, TEXT_COLOR);
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

    private final class OpacitySlider extends AbstractSlider {
        private OpacitySlider(int x, int y, int width, int height, int initialValue) {
            super(x, y, width, height, StringTextComponent.EMPTY, MathHelper.clamp(initialValue, 0, 100) / 100.0);
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(new TranslationTextComponent(
                    "options.generic_value",
                    new TranslationTextComponent("commontrades.configuration.visualMarkers.outlineOpacity"),
                    new StringTextComponent(Integer.toString(CommonTradesConfigScreen.this.outlineOpacity) + "%")));
        }

        @Override
        protected void applyValue() {
            CommonTradesConfigScreen.this.outlineOpacity = MathHelper.clamp((int) Math.round(this.value * 100.0), 0, 100);
            updateMessage();
        }
    }
}

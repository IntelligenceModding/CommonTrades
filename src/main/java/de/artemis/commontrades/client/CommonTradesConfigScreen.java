package de.artemis.commontrades.client;

import com.mojang.blaze3d.vertex.PoseStack;
import de.artemis.commontrades.config.CommonTradesClientConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public final class CommonTradesConfigScreen extends Screen {
    private static final int ROW_HEIGHT = 24;
    private static final int BUTTON_HEIGHT = 20;
    private static final int CONTROL_WIDTH = 138;
    private static final int TITLE_COLOR = 0xFFFFFFFF;
    private static final int SUBTITLE_COLOR = 0xFFA0A0A0;
    private static final int LABEL_COLOR = 0xFFE0E0E0;
    private static final int STATUS_COLOR = 0xFFA0E0A0;
    private static final int INVALID_TEXT_COLOR = 0xFFFF7070;

    private final Screen parent;
    private final Draft draft;
    private final List<RenderedLabel> labels = new ArrayList<>();
    private Component status = TextComponent.EMPTY;

    public CommonTradesConfigScreen(Screen parent) {
        super(new TranslatableComponent("commontrades.config.title"));
        this.parent = parent;
        this.draft = new Draft(CommonTradesClientConfig.snapshot());
    }

    @Override
    protected void init() {
        rebuild();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);
        drawCenteredString(poseStack, this.font, this.title, this.width / 2, 12, TITLE_COLOR);
        drawCenteredString(poseStack, this.font, new TranslatableComponent("commontrades.config.client"), this.width / 2, 34, SUBTITLE_COLOR);
        for (RenderedLabel label : this.labels) {
            drawString(poseStack, this.font, label.component(), label.x(), label.y(), LABEL_COLOR);
        }
        if (!this.status.getString().isEmpty()) {
            drawCenteredString(poseStack, this.font, this.status, this.width / 2, this.height - 54, STATUS_COLOR);
        }
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parent);
        }
    }

    private void rebuild() {
        this.clearWidgets();
        this.labels.clear();

        int centerX = this.width / 2;
        List<Option> options = currentOptions();
        int rowTop = 72;
        int rowsPerPage = Math.max(1, (this.height - rowTop - 72) / ROW_HEIGHT);
        int visibleRows = Math.min(options.size(), rowsPerPage);
        for (int index = 0; index < visibleRows; index++) {
            addOption(options.get(index), rowTop + index * ROW_HEIGHT);
        }

        this.addRenderableWidget(new Button(centerX - 154, this.height - 28, 96, BUTTON_HEIGHT, new TranslatableComponent("commontrades.config.save"), button -> save()));
        this.addRenderableWidget(new Button(centerX - 48, this.height - 28, 96, BUTTON_HEIGHT, new TranslatableComponent("commontrades.config.reload"), button -> reload()));
        this.addRenderableWidget(new Button(centerX + 58, this.height - 28, 96, BUTTON_HEIGHT, new TranslatableComponent("commontrades.config.done"), button -> onClose()));
    }

    private void addOption(Option option, int y) {
        int labelX = this.width / 2 - 174;
        int controlX = this.width / 2 + 40;
        Component label = new TranslatableComponent(option.labelKey());
        this.labels.add(new RenderedLabel(label, labelX, y + 6));

        if (option instanceof BooleanOption booleanOption) {
            Button button = new Button(controlX, y, CONTROL_WIDTH, BUTTON_HEIGHT, booleanValue(booleanOption.getter().getAsBoolean()), pressed -> {
                        booleanOption.setter().accept(!booleanOption.getter().getAsBoolean());
                        this.status = TextComponent.EMPTY;
                        pressed.setMessage(booleanValue(booleanOption.getter().getAsBoolean()));
                    });
            this.addRenderableWidget(button);
            return;
        }

        if (option instanceof IntOption intOption) {
            EditBox editBox = new EditBox(this.font, controlX, y, CONTROL_WIDTH, BUTTON_HEIGHT, label);
            editBox.setValue(Integer.toString(intOption.getter().getAsInt()));
            editBox.setResponder(value -> {
                try {
                    int parsed = Integer.parseInt(value);
                    boolean valid = parsed >= intOption.min() && parsed <= intOption.max();
                    editBox.setTextColor(valid ? LABEL_COLOR : INVALID_TEXT_COLOR);
                    if (valid) {
                        intOption.setter().accept(parsed);
                        this.status = TextComponent.EMPTY;
                    }
                } catch (NumberFormatException ignored) {
                    editBox.setTextColor(INVALID_TEXT_COLOR);
                }
            });
            this.addRenderableWidget(editBox);
            return;
        }

        if (option instanceof TextOption textOption) {
            EditBox editBox = new EditBox(this.font, controlX, y, CONTROL_WIDTH, BUTTON_HEIGHT, label);
            editBox.setValue(textOption.getter().get());
            editBox.setResponder(value -> {
                boolean valid = textOption.validator().test(value);
                editBox.setTextColor(valid ? LABEL_COLOR : INVALID_TEXT_COLOR);
                if (valid) {
                    textOption.setter().accept(value);
                    this.status = TextComponent.EMPTY;
                }
            });
            this.addRenderableWidget(editBox);
        }
    }

    private List<Option> currentOptions() {
        return List.of(
                new BooleanOption("commontrades.configuration.visualMarkers.visualIndicators", () -> this.draft.visualIndicators, value -> this.draft.visualIndicators = value),
                new IntOption("commontrades.configuration.visualMarkers.outlineOpacity", () -> this.draft.outlineOpacity, value -> this.draft.outlineOpacity = value, 0, 100),
                new TextOption("commontrades.configuration.visualMarkers.outlineColor", () -> this.draft.outlineColor, value -> this.draft.outlineColor = value, CommonTradesClientConfig::isColorCode)
        );
    }

    private void save() {
        CommonTradesClientConfig.apply(this.draft.clientSnapshot());
        this.status = new TranslatableComponent("commontrades.config.saved");
    }

    private void reload() {
        CommonTradesClientConfig.load();
        this.draft.load(CommonTradesClientConfig.snapshot());
        this.status = new TranslatableComponent("commontrades.config.reloaded");
        rebuild();
    }

    private static Component booleanValue(boolean value) {
        return new TextComponent(value ? "On" : "Off");
    }

    private sealed interface Option permits BooleanOption, IntOption, TextOption {
        String labelKey();
    }

    private record BooleanOption(String labelKey, BooleanSupplier getter, Consumer<Boolean> setter) implements Option {
    }

    private record IntOption(String labelKey, IntSupplier getter, IntConsumer setter, int min, int max) implements Option {
    }

    private record TextOption(String labelKey, TextSupplier getter, Consumer<String> setter, TextValidator validator) implements Option {
    }

    private record RenderedLabel(Component component, int x, int y) {
    }

    @FunctionalInterface
    private interface TextSupplier {
        String get();
    }

    @FunctionalInterface
    private interface TextValidator {
        boolean test(String value);
    }

    private static final class Draft {
        boolean visualIndicators;
        int outlineOpacity;
        String outlineColor;

        private Draft(CommonTradesClientConfig.Snapshot client) {
            load(client);
        }

        private void load(CommonTradesClientConfig.Snapshot client) {
            this.visualIndicators = client.visualIndicators();
            this.outlineOpacity = client.outlineOpacity();
            this.outlineColor = client.outlineColor();
        }

        private CommonTradesClientConfig.Snapshot clientSnapshot() {
            return new CommonTradesClientConfig.Snapshot(this.visualIndicators, this.outlineOpacity, this.outlineColor);
        }
    }
}

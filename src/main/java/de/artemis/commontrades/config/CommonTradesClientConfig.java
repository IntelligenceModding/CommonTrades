package de.artemis.commontrades.config;

import java.util.Locale;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public final class CommonTradesClientConfig {
    public static final CommonTradesClientConfig INSTANCE;
    public static final ForgeConfigSpec SPEC;

    private static final int DEFAULT_OUTLINE_RGB = 0x00D26A;
    private static final String DEFAULT_OUTLINE_COLOR = "#00D26A";

    private final ForgeConfigSpec.BooleanValue visualIndicators;
    private final ForgeConfigSpec.IntValue outlineOpacity;
    private final ForgeConfigSpec.ConfigValue<String> outlineColor;

    static {
        Pair<CommonTradesClientConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(CommonTradesClientConfig::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }

    private CommonTradesClientConfig(ForgeConfigSpec.Builder builder) {
        builder.push("visualMarkers");
        visualIndicators = builder
                .translation("commontrades.configuration.visualMarkers.visualIndicators")
                .comment("Client-side. Show Common Trades visual markers and the tooltip line in Wandering Trader trades.")
                .define("visualIndicators", true);
        outlineOpacity = builder
                .translation("commontrades.configuration.visualMarkers.outlineOpacity")
                .comment("Client-side. Opacity of the Common Trades trade-row outline, as a percentage.")
                .defineInRange("outlineOpacity", 100, 0, 100);
        outlineColor = builder
                .translation("commontrades.configuration.visualMarkers.outlineColor")
                .comment("Client-side. RGB hex color for the Common Trades trade-row outline. Accepted forms: #00D26A, 00D26A, or 0x00D26A.")
                .define("outlineColor", DEFAULT_OUTLINE_COLOR, CommonTradesClientConfig::isColorCode);
        builder.pop();
    }

    public static boolean visualIndicators() {
        return get(INSTANCE.visualIndicators);
    }

    public static int outlineOpacity() {
        return get(INSTANCE.outlineOpacity);
    }

    public static String outlineColorText() {
        return get(INSTANCE.outlineColor);
    }

    public static int outlineColor() {
        int alpha = Math.round(outlineOpacity() * 255.0F / 100.0F);
        int rgb = parseRgb(outlineColorText(), DEFAULT_OUTLINE_RGB);
        return alpha << 24 | rgb;
    }

    public static boolean isColorCode(Object value) {
        return value instanceof String && parseRgb((String) value, -1) >= 0;
    }

    public static void saveVisualMarkerSettings(boolean visualIndicators, int outlineOpacity, String outlineColor) {
        if (!isColorCode(outlineColor)) {
            return;
        }

        INSTANCE.visualIndicators.set(visualIndicators);
        INSTANCE.outlineOpacity.set(clamp(outlineOpacity, 0, 100));
        INSTANCE.outlineColor.set(normalizeColor(outlineColor));
        SPEC.save();
    }

    private static int parseRgb(String text, int fallback) {
        String normalized = text.trim().toLowerCase(Locale.ROOT);
        if (normalized.startsWith("#")) {
            normalized = normalized.substring(1);
        } else if (normalized.startsWith("0x")) {
            normalized = normalized.substring(2);
        }

        if (normalized.length() != 6) {
            return fallback;
        }
        for (int index = 0; index < normalized.length(); index++) {
            char character = normalized.charAt(index);
            if ((character < '0' || character > '9') && (character < 'a' || character > 'f')) {
                return fallback;
            }
        }
        return Integer.parseInt(normalized, 16);
    }

    private static String normalizeColor(String text) {
        String normalized = text.trim();
        if (normalized.startsWith("0x") || normalized.startsWith("0X")) {
            normalized = normalized.substring(2);
        }
        if (!normalized.startsWith("#")) {
            normalized = "#" + normalized;
        }
        return normalized.toUpperCase(Locale.ROOT);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static boolean get(ForgeConfigSpec.BooleanValue value) {
        return value.get();
    }

    private static int get(ForgeConfigSpec.IntValue value) {
        return value.get();
    }

    private static String get(ForgeConfigSpec.ConfigValue<String> value) {
        return value.get();
    }
}

package de.artemis.commontrades.config;

import java.util.Locale;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public final class CommonTradesClientConfig {
    public static final CommonTradesClientConfig INSTANCE;
    public static final ModConfigSpec SPEC;

    private static final int DEFAULT_OUTLINE_RGB = 0x00D26A;
    private static final String DEFAULT_OUTLINE_COLOR = "#00D26A";

    private final ModConfigSpec.BooleanValue visualIndicators;
    private final ModConfigSpec.IntValue outlineOpacity;
    private final ModConfigSpec.ConfigValue<String> outlineColor;

    static {
        Pair<CommonTradesClientConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(CommonTradesClientConfig::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }

    private CommonTradesClientConfig(ModConfigSpec.Builder builder) {
        builder.translation("commontrades.configuration.visualMarkers").push("visualMarkers");
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

    public static int outlineColor() {
        int alpha = Math.round(get(INSTANCE.outlineOpacity) * 255.0F / 100.0F);
        int rgb = parseRgb(get(INSTANCE.outlineColor), DEFAULT_OUTLINE_RGB);
        return alpha << 24 | rgb;
    }

    private static boolean isColorCode(Object value) {
        return value instanceof String text && parseRgb(text, -1) >= 0;
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

    private static boolean get(ModConfigSpec.BooleanValue value) {
        return SPEC.isLoaded() ? value.getAsBoolean() : value.getDefault();
    }

    private static int get(ModConfigSpec.IntValue value) {
        return SPEC.isLoaded() ? value.getAsInt() : value.getDefault();
    }

    private static String get(ModConfigSpec.ConfigValue<String> value) {
        return SPEC.isLoaded() ? value.get() : value.getDefault();
    }
}

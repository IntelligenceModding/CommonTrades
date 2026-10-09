package de.artemis.commontrades.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.artemis.commontrades.CommonTrades;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import net.fabricmc.loader.api.FabricLoader;

public final class CommonTradesClientConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final int DEFAULT_OUTLINE_RGB = 0x00D26A;
    private static final String DEFAULT_OUTLINE_COLOR = "#00D26A";
    private static Values values = new Values();

    private CommonTradesClientConfig() {
    }

    public static void load() {
        Path path = path();
        if (Files.exists(path)) {
            try (Reader reader = Files.newBufferedReader(path)) {
                Values loaded = GSON.fromJson(reader, Values.class);
                if (loaded != null) {
                    values = loaded.normalized();
                    return;
                }
            } catch (IOException exception) {
                CommonTrades.LOGGER.warn("Could not load Common Trades client config at {}", path, exception);
            }
        }
        save(path);
    }

    public static Snapshot snapshot() {
        return Snapshot.from(values);
    }

    public static void apply(Snapshot snapshot) {
        values = snapshot.toValues().normalized();
        save(path());
    }

    public static boolean visualIndicators() {
        return values.visualIndicators;
    }

    public static int outlineOpacity() {
        return values.outlineOpacity;
    }

    public static String outlineColorText() {
        return values.outlineColor;
    }

    public static int outlineColor() {
        int alpha = Math.round(values.outlineOpacity * 255.0F / 100.0F);
        int rgb = parseRgb(values.outlineColor, DEFAULT_OUTLINE_RGB);
        return alpha << 24 | rgb;
    }

    public static boolean isColorCode(String value) {
        return parseRgb(value, -1) >= 0;
    }

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve(CommonTrades.MOD_ID + "-client.json");
    }

    private static void save(Path path) {
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path)) {
                GSON.toJson(values, writer);
            }
        } catch (IOException exception) {
            CommonTrades.LOGGER.warn("Could not save Common Trades client config at {}", path, exception);
        }
    }

    private static int parseRgb(String text, int fallback) {
        if (text == null) {
            return fallback;
        }
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

    public record Snapshot(boolean visualIndicators, int outlineOpacity, String outlineColor) {
        private static Snapshot from(Values values) {
            return new Snapshot(values.visualIndicators, values.outlineOpacity, values.outlineColor);
        }

        private Values toValues() {
            Values result = new Values();
            result.visualIndicators = visualIndicators;
            result.outlineOpacity = outlineOpacity;
            result.outlineColor = outlineColor;
            return result;
        }
    }

    private static final class Values {
        boolean visualIndicators = true;
        int outlineOpacity = 100;
        String outlineColor = DEFAULT_OUTLINE_COLOR;

        Values normalized() {
            outlineOpacity = Math.max(0, Math.min(100, outlineOpacity));
            if (!isColorCode(outlineColor)) {
                outlineColor = DEFAULT_OUTLINE_COLOR;
            }
            return this;
        }
    }
}

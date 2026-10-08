package de.artemis.commontrades.client;

import de.artemis.commontrades.CommonTrades;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;

public final class CommonTradesClientSetup {
    private CommonTradesClientSetup() {
    }

    public static void registerConfigScreen() {
        ModLoadingContext.get().registerExtensionPoint(
                ExtensionPoint.CONFIGGUIFACTORY,
                () -> (minecraft, parent) -> new CommonTradesConfigScreen(parent));
        CommonTrades.LOGGER.debug("Registered Common Trades Forge config screen.");
    }
}

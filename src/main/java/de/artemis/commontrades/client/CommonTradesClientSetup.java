package de.artemis.commontrades.client;

import de.artemis.commontrades.CommonTrades;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.fml.ModLoadingContext;

public final class CommonTradesClientSetup {
    private CommonTradesClientSetup() {
    }

    public static void registerConfigScreen() {
        ModLoadingContext.get().registerExtensionPoint(
                ConfigGuiHandler.ConfigGuiFactory.class,
                () -> new ConfigGuiHandler.ConfigGuiFactory((minecraft, parent) -> new CommonTradesConfigScreen(parent)));
        CommonTrades.LOGGER.debug("Registered Common Trades Forge config screen.");
    }
}

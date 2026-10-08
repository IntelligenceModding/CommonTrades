package de.artemis.commontrades.client;

import de.artemis.commontrades.CommonTrades;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public final class CommonTradesClientSetup {
    private CommonTradesClientSetup() {
    }

    public static void registerConfigScreen(FMLJavaModLoadingContext context) {
        context.registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(CommonTradesConfigScreen::new));
        CommonTrades.LOGGER.debug("Registered Common Trades Forge config screen.");
    }
}

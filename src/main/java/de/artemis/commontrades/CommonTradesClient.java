package de.artemis.commontrades;

import de.artemis.commontrades.config.CommonTradesClientConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = CommonTrades.MOD_ID, dist = Dist.CLIENT)
public final class CommonTradesClient {
    public CommonTradesClient(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, CommonTradesClientConfig.SPEC);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, (container, parent) -> new ConfigurationScreen(container, parent));
    }
}

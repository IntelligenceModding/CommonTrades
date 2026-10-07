package de.artemis.commontrades;

import com.mojang.logging.LogUtils;
import de.artemis.commontrades.command.CommonTradesCommands;
import de.artemis.commontrades.config.CommonTradesConfig;
import de.artemis.commontrades.network.CommonTradesNetwork;
import de.artemis.commontrades.trade.TradePoolCache;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(CommonTrades.MOD_ID)
public final class CommonTrades {
    public static final String MOD_ID = "commontrades";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CommonTrades(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, CommonTradesConfig.SPEC);
        modEventBus.addListener(CommonTradesNetwork::registerPayloads);
        modEventBus.addListener(TradePoolCache::onConfigLoading);
        modEventBus.addListener(TradePoolCache::onConfigReloading);

        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, TradePoolCache::onTagsUpdated);
        NeoForge.EVENT_BUS.addListener(CommonTradesCommands::register);

        LOGGER.info("Loading Common Trades");
    }
}

package de.artemis.commontrades;

import com.mojang.logging.LogUtils;
import de.artemis.commontrades.command.CommonTradesCommands;
import de.artemis.commontrades.config.CommonTradesConfig;
import de.artemis.commontrades.network.CommonTradesNetwork;
import de.artemis.commontrades.trade.TradePoolCache;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import org.slf4j.Logger;

public final class CommonTrades implements ModInitializer {
    public static final String MOD_ID = "commontrades";
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        CommonTradesConfig.load();
        CommonTradesNetwork.registerPayloads();
        CommonTradesCommands.register();
        CommonLifecycleEvents.TAGS_LOADED.register((registries, client) -> {
            if (!client) {
                TradePoolCache.onTagsLoaded(registries);
            }
        });
        LOGGER.info("Loading Common Trades");
    }
}

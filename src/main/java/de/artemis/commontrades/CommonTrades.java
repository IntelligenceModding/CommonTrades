package de.artemis.commontrades;

import com.mojang.logging.LogUtils;
import de.artemis.commontrades.client.CommonTradesClientSetup;
import de.artemis.commontrades.command.CommonTradesCommands;
import de.artemis.commontrades.config.CommonTradesClientConfig;
import de.artemis.commontrades.config.CommonTradesConfig;
import de.artemis.commontrades.network.CommonTradesNetwork;
import de.artemis.commontrades.trade.TradePoolCache;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(CommonTrades.MOD_ID)
public final class CommonTrades {
    public static final String MOD_ID = "commontrades";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CommonTrades(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        context.registerConfig(ModConfig.Type.SERVER, CommonTradesConfig.SPEC);
        context.registerConfig(ModConfig.Type.CLIENT, CommonTradesClientConfig.SPEC);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CommonTradesClientSetup.registerConfigScreen(context));
        CommonTradesNetwork.registerPayloads();
        modEventBus.addListener(TradePoolCache::onConfigLoading);
        modEventBus.addListener(TradePoolCache::onConfigReloading);

        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, TradePoolCache::onTagsUpdated);
        MinecraftForge.EVENT_BUS.addListener(CommonTradesCommands::register);

        LOGGER.info("Loading Common Trades");
    }
}

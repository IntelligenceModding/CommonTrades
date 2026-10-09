package de.artemis.commontrades;

import de.artemis.commontrades.config.CommonTradesClientConfig;
import de.artemis.commontrades.network.CommonTradesNetwork;
import net.fabricmc.api.ClientModInitializer;

public final class CommonTradesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CommonTradesClientConfig.load();
        CommonTradesNetwork.registerClientPayloads();
    }
}

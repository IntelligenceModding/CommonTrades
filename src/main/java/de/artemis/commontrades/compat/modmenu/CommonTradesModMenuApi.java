package de.artemis.commontrades.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import de.artemis.commontrades.client.CommonTradesConfigScreen;

public final class CommonTradesModMenuApi implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return CommonTradesConfigScreen::new;
    }
}

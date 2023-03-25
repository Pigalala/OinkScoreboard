package me.pigalala.oinkscoreboard;

import me.pigalala.oinkscoreboard.config.OinkConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class OinkScoreboard implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        OinkConfig.load();
    }
}

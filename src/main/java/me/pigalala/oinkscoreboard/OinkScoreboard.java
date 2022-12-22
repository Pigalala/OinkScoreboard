package me.pigalala.oinkscoreboard;

import me.pigalala.oinkscoreboard.config.OinkConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class OinkScoreboard implements ClientModInitializer {
    public static OinkConfig config;

    @Override
    public void onInitializeClient() {
        AutoConfig.register(OinkConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(OinkConfig.class).getConfig();
    }
}

package me.pigalala.oinkscoreboard;

import me.pigalala.oinkscoreboard.config.OinkConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public final class OinkScoreboard implements ClientModInitializer {

    public static OinkConfig config;

    private final KeyBinding toggleScoreboard = new KeyBinding("Toggle Scoreboard", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O, "OinkScoreboard");

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(toggleScoreboard);
        config = new OinkConfig();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(toggleScoreboard.wasPressed()) {
                config.enabled = !config.enabled;
                config.save(); // Silly if you spam
            }
        });
    }
}

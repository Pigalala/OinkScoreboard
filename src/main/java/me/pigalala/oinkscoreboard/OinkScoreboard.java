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
public class OinkScoreboard implements ClientModInitializer {

    private static KeyBinding enlargeScoreboardKey;
    private static KeyBinding dwindleScoreboardKey;

    @Override
    public void onInitializeClient() {
        OinkConfig.load();

        enlargeScoreboardKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.oinkscoreboard.enlarge",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UP,
                "category.oinkscoreboard.keys"
        ));
        dwindleScoreboardKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.oinkscoreboard.dwindle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_DOWN,
                "category.oinkscoreboard.keys"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while(enlargeScoreboardKey.wasPressed()) OinkConfig.maxRows++;
            while(dwindleScoreboardKey.wasPressed()) OinkConfig.maxRows--;

            if(OinkConfig.maxRows > 99) OinkConfig.maxRows = 99;
            else OinkConfig.maxRows = Math.max(1, OinkConfig.maxRows);
        });
    }
}

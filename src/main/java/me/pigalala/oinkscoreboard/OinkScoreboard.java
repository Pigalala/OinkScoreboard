package me.pigalala.oinkscoreboard;

import me.pigalala.oinkscoreboard.config.OinkConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class OinkScoreboard implements ClientModInitializer {

    private final KeyBinding toggleScoreboard = new KeyBinding("Toggle Scoreboard", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O, "OinkScoreboard");

    @Override
    public void onInitializeClient() {
        OinkConfig.load();
        KeyBindingHelper.registerKeyBinding(toggleScoreboard);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(toggleScoreboard.wasPressed())
                OinkConfig.enabled = !OinkConfig.enabled;
        });
    }
}

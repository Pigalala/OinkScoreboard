package me.pigalala.oinkscoreboard;

import me.pigalala.oinkscoreboard.config.OinkConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class OinkScoreboard implements ClientModInitializer {

    private static final Identifier MESSAGE_CHANNEL = new Identifier("oinkscoreboard:settings");

    @Override
    public void onInitializeClient() {
        OinkConfig.load();

        ClientPlayConnectionEvents.JOIN.register((clientPlayNetworkHandler, packetSender, client) -> {
            if (client != MinecraftClient.getInstance()) return;
            OinkScoreboard.sendRowsPacket();
        });
    }

    public static void sendRowsPacket() {
        PacketByteBuf packet = PacketByteBufs.create();
        packet.writeByte(0);
        packet.writeByte(OinkConfig.maxRows);
        ClientPlayNetworking.send(MESSAGE_CHANNEL, packet);
    }
}

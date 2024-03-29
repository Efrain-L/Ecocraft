package net.syn100.ecocraft.setup;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.syn100.ecocraft.EcoCraft;
import net.syn100.ecocraft.emissionsystem.network.PacketSyncEmissionsToClient;
import net.syn100.ecocraft.emissionsystem.network.PacketSyncPollutionsToClient;

public class Messages {
    private static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int id() { return packetId++; }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(EcoCraft.MOD_ID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(PacketSyncEmissionsToClient.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(PacketSyncEmissionsToClient::new)
                .encoder(PacketSyncEmissionsToClient::toBytes)
                .consumerMainThread(PacketSyncEmissionsToClient::handle)
                .add();
        net.messageBuilder(PacketSyncPollutionsToClient.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(PacketSyncPollutionsToClient::new)
                .encoder(PacketSyncPollutionsToClient::toBytes)
                .consumerMainThread(PacketSyncPollutionsToClient::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}

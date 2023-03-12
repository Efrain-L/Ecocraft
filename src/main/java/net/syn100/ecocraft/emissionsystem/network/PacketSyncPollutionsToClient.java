package net.syn100.ecocraft.emissionsystem.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.syn100.ecocraft.emissionsystem.client.ClientPollutionData;

import java.util.function.Supplier;

public class PacketSyncPollutionsToClient {
    private final float chunkPollutions;

    public PacketSyncPollutionsToClient(float chunkPollutions) {
        this.chunkPollutions = chunkPollutions;
    }

    public PacketSyncPollutionsToClient(FriendlyByteBuf buf) {
        chunkPollutions = buf.readFloat();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeFloat(chunkPollutions);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ClientPollutionData.set(chunkPollutions);
        });
        return true;
    }
}

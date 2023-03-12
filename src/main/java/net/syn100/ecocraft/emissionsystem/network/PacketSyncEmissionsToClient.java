package net.syn100.ecocraft.emissionsystem.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.syn100.ecocraft.emissionsystem.client.ClientEmissionData;

import java.util.function.Supplier;

public class PacketSyncEmissionsToClient {
    private final float chunkEmissions;

    public PacketSyncEmissionsToClient(float chunkEmissions) {
        this.chunkEmissions = chunkEmissions;
    }

    public PacketSyncEmissionsToClient(FriendlyByteBuf buf) {
        chunkEmissions = buf.readFloat();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeFloat(chunkEmissions);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ClientEmissionData.set(chunkEmissions);
        });
        return true;
    }
}

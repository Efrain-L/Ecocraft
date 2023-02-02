package net.syn100.ecocraft.emissionsystem.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.syn100.ecocraft.emissionsystem.client.ClientEmissionData;

import java.util.function.Supplier;

public class PacketSyncEmissionsToClient {
    private final int chunkEmissions;

    public PacketSyncEmissionsToClient(int chunkEmissions) {
        this.chunkEmissions = chunkEmissions;
    }

    public PacketSyncEmissionsToClient(FriendlyByteBuf buf) {
        chunkEmissions = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(chunkEmissions);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ClientEmissionData.set(chunkEmissions);
        });
        return true;
    }
}

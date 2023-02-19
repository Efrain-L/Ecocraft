package net.syn100.ecocraft.emissionsystem.data;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.syn100.ecocraft.emissionsystem.EmissionConfig;
import net.syn100.ecocraft.emissionsystem.network.PacketSyncEmissionsToClient;
import net.syn100.ecocraft.setup.Messages;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class EmissionManager extends SavedData {

    private final Map<ChunkPos, Emissions> emissionsMap = new HashMap<>();

    @Nonnull
    public static EmissionManager get(Level level) {
        // This method should not be able to access the client, just the world's server.
        if (level.isClientSide) {
            throw new RuntimeException("No client side access");
        }
        DimensionDataStorage storage = ((ServerLevel)level).getDataStorage();
        return storage.computeIfAbsent(EmissionManager::new, EmissionManager::new, "emissionmanager");
    }

    @Nonnull
    private Emissions getEmissionsInternal(BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);
        return emissionsMap
                .computeIfAbsent(chunkPos, cp ->
                    new Emissions(EmissionConfig.CHUNK_MIN_EMISSIONS.get())
                );
    }

    public float getEmissions(BlockPos pos) {
        Emissions emissions = getEmissionsInternal(pos);
        return emissions.getEmissions();
    }

    /**
     * Defining this method differently from the tutorial, adding a parameter
     * for the amount to remove from the chunk's emissions value.
     */
    public float decreaseEmissions(BlockPos pos, float remove) {
        Emissions emissions = getEmissionsInternal(pos);
        float currentEmissions = emissions.getEmissions();
        if (currentEmissions - remove >= 0) {
            emissions.setEmissions(currentEmissions - remove);
            // Set dirty is needed to save changes to the chunk
            setDirty();
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Same goes for the increase method
     */
    public float increaseEmissions(BlockPos pos, float add) {
        Emissions emissions = getEmissionsInternal(pos);
        float currentEmissions = emissions.getEmissions();
        if (currentEmissions >= 0) {
            emissions.setEmissions(currentEmissions + add);
            // Set dirty is needed to save changes to the chunk
            setDirty();
            return 1;
        } else {
            return 0;
        }
    }

    public void tick(Level level) {
        level.players().forEach(player -> {
            if (player instanceof ServerPlayer serverPlayer) {
                float chunkEmissions = getEmissions(serverPlayer.blockPosition());
                Messages.sendToPlayer(new PacketSyncEmissionsToClient(chunkEmissions), serverPlayer);
            }
        });
    }

    public EmissionManager() {

    }

    public EmissionManager(CompoundTag tag) {
        ListTag list = tag.getList("emissions", Tag.TAG_COMPOUND);
        for (Tag t: list) {
            CompoundTag emissionTag = (CompoundTag) t;
            Emissions emissions = new Emissions(emissionTag.getFloat("emissions"));
            ChunkPos pos = new ChunkPos(emissionTag.getInt("x"), emissionTag.getInt("z"));
            emissionsMap.put(pos, emissions);
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        emissionsMap.forEach(((chunkPos, emissions) -> {
            CompoundTag emissionTag = new CompoundTag();
            emissionTag.putInt("x", chunkPos.x);
            emissionTag.putInt("z", chunkPos.z);
            emissionTag.putFloat("emissions", emissions.getEmissions());
            list.add(emissionTag);
        }));
        tag.put("emissions", list);
        return tag;
    }
}

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
import net.syn100.ecocraft.effect.EmissionEffects;
import net.syn100.ecocraft.emissionsystem.EmissionConfig;
import net.syn100.ecocraft.emissionsystem.network.PacketSyncEmissionsToClient;
import net.syn100.ecocraft.setup.Messages;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EmissionManager extends SavedData {

    private final Map<ChunkPos, Emissions> emissionsMap = new HashMap<>();
    private boolean emissionSpreading = false; // DEBUG: just for demo purposes, default to false

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

    @Nonnull
    private boolean getEmissionSpreading() {
        return this.emissionSpreading;
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
        emissions.setEmissions(currentEmissions - remove);
        // Set dirty is needed to save changes to the chunk
        setDirty();
        return 1;
    }

    /**
     * Same goes for the increase method
     */
    public float increaseEmissions(BlockPos pos, float add) {
        Emissions emissions = getEmissionsInternal(pos);
        float currentEmissions = emissions.getEmissions();
            emissions.setEmissions(currentEmissions + add);
            // Set dirty is needed to save changes to the chunk
            setDirty();
            return 1;
    }

    public int setEmissionSpreading(boolean bool) {
        this.emissionSpreading = bool;
        return 1;
    }

    public float spreadEmissions(BlockPos pos) {
        Emissions emissions = getEmissionsInternal(pos);
        // The spreading starts only when the emission in this chunk exceeds this minSpreadingLevel.
        int minSpreadingLevel = 20; // Aims to prevent infinite spreading loop between chunks
        float currentEmissions = emissions.getEmissions();
        float percentSpreadPerTick = (float)0.001; // The percentage of emission to be spread every tick

        if (currentEmissions >= minSpreadingLevel) {
            float totalEmissionsSpread = currentEmissions * percentSpreadPerTick;
            // The amount of emission that each adjacent chunk receives
            float receivedEmissions = totalEmissionsSpread / 8;
            this.decreaseEmissions(pos, totalEmissionsSpread);

            // Spread to the eight adjacent chunks
            for(int x=-1;x<=1;x+=1) {
                for(int z=-1;z<=1;z+=1) {
                    if(x==0 && z==0) continue;
                    BlockPos spreadTargetPos = pos.offset(16 * x, 0, 16 * z);
                    this.increaseEmissions(spreadTargetPos, receivedEmissions);
                }
            }
            // Set dirty is needed to save changes to the chunk
            setDirty();
            return 1;
        } else {
            return 0;
        }
    }

    public void tick(Level level) {
        // Spreading logic
        if(getEmissionSpreading()) {
            ArrayList<ChunkPos> spreadChunkPos = new ArrayList<>(emissionsMap.keySet());
            spreadChunkPos.forEach(chunkPos -> {
                BlockPos spreadPos = new BlockPos(chunkPos.getMiddleBlockX(), 0, chunkPos.getMiddleBlockZ());
                this.spreadEmissions(spreadPos);
                // Adding ash particles
                EmissionEffects.UpdateAshEffects((ServerLevel) level, spreadPos);
            });
        }
        // Send emission info to client for updating GUI
        level.players().forEach(player -> {
            if (player instanceof ServerPlayer serverPlayer) {
                float chunkEmissions = getEmissions(serverPlayer.blockPosition());
                Messages.sendToPlayer(new PacketSyncEmissionsToClient(chunkEmissions), serverPlayer);
            }
        });
        // Update potion effects for entities
        EmissionEffects.UpdateEffects((ServerLevel) level);
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

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
import net.syn100.ecocraft.emissionsystem.PollutionConfig;
import net.syn100.ecocraft.emissionsystem.network.PacketSyncPollutionsToClient;
import net.syn100.ecocraft.setup.Messages;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PollutionManager extends SavedData {
    /**
     * This is done very similar to the EmissionManager and the tutorial
     */
    private final Map<ChunkPos, Pollutions> pollutionsMap = new HashMap<>();

    @Nonnull
    public static PollutionManager get(Level level) {
        // This method should not be able to access the client, just the world's server.
        if (level.isClientSide) {
            throw new RuntimeException("No client side access");
        }
        DimensionDataStorage storage = ((ServerLevel)level).getDataStorage();
        return storage.computeIfAbsent(PollutionManager::new, PollutionManager::new, "pollutionmanager");
    }

    @Nonnull
    private Pollutions getPollutionsInternal(BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);
        return pollutionsMap.computeIfAbsent(
                chunkPos, cp -> new Pollutions(PollutionConfig.CHUNK_MIN_POLLUTIONS.get())
        );
    }

    public float getPollutions(BlockPos pos) {
        Pollutions pollutions = getPollutionsInternal(pos);
        return pollutions.getPollutions();
    }

    public float decreasePollutions(BlockPos pos, float remove) {
        Pollutions pollutions = getPollutionsInternal(pos);
        float currentPollutions = pollutions.getPollutions();
        if (currentPollutions - remove >= 0) {
            pollutions.setPollutions(currentPollutions - remove);
            // Set dirty is needed to save changes to the chunk
            setDirty();
            return 1;
        } else {
            return 0;
        }
    }

    public float increasePollutions(BlockPos pos, float add) {
        Pollutions pollutions = getPollutionsInternal(pos);
        float currentPollutions = pollutions.getPollutions();
        if (currentPollutions >= 0) {
            pollutions.setPollutions(currentPollutions + add);
            // Set dirty is needed to save changes to the chunk
            setDirty();
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Same spread method with the EmissionManager
     */
    public float spreadPollutions(BlockPos pos) {
        Pollutions pollutions = getPollutionsInternal(pos);
        // The spreading starts only when the pollution in this chunk exceeds this minSpreadingLevel.
        int minSpreadingLevel = 10; // Aims to prevent infinite spreading loop between chunks
        float currentPollutions = pollutions.getPollutions();
        float percentSpreadPerTick = (float)0.001; // The percentage of pollution to be spread every tick

        if (currentPollutions >= minSpreadingLevel) {
            float totalPollutionsSpread = currentPollutions * percentSpreadPerTick;
            // The amount of pollution that each adjacent chunk receives
            float receivedPollutions = totalPollutionsSpread / 9;
            this.decreasePollutions(pos, totalPollutionsSpread);

            // Spread to the eight adjacent chunks
            for(int x=-1;x<=1;x+=1) {
                for(int z=-1;z<=1;z+=1) {
                    BlockPos spreadTargetPos = pos.offset(16 * x, 0, 16 * z);
                    this.increasePollutions(spreadTargetPos, receivedPollutions);
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
        ArrayList<ChunkPos> spreadChunkPos = new ArrayList<ChunkPos>(pollutionsMap.keySet());
        spreadChunkPos.forEach(chunkPos -> {
            BlockPos spreadPos = new BlockPos(chunkPos.getMiddleBlockX(), 0 ,chunkPos.getMiddleBlockZ());
            this.spreadPollutions(spreadPos);
        });
        level.players().forEach(player -> {
            if (player instanceof ServerPlayer serverPlayer) {
                float chunkPollutions = getPollutions(serverPlayer.blockPosition());
                Messages.sendToPlayer(new PacketSyncPollutionsToClient(chunkPollutions), serverPlayer);
            }
        });
    }

    public PollutionManager() {

    }

    public PollutionManager(CompoundTag tag) {
        ListTag list = tag.getList("pollutions", Tag.TAG_COMPOUND);
        for (Tag t: list) {
            CompoundTag pollutionTag = (CompoundTag) t;
            Pollutions pollutions = new Pollutions(pollutionTag.getFloat("pollutions"));
            ChunkPos pos = new ChunkPos(pollutionTag.getInt("x"), pollutionTag.getInt("z"));
            pollutionsMap.put(pos, pollutions);
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        pollutionsMap.forEach(((chunkPos, pollutions) -> {
            CompoundTag pollutionTag = new CompoundTag();
            pollutionTag.putInt("x", chunkPos.x);
            pollutionTag.putInt("z", chunkPos.z);
            pollutionTag.putFloat("pollutions", pollutions.getPollutions());
            list.add(pollutionTag);
        }));
        tag.put("pollutions", list);
        return tag;
    }
}

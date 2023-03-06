package net.syn100.ecocraft.events;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.syn100.ecocraft.emissionsystem.data.EmissionManager;

public class CropGrowingEvents {
    // Default growth chance is approximately 1 in 13 according to Minecraft wiki
    private static final float DEFAULT_CHANCE = 1.0f / 13.0f;

    // Arbitrary values for now
    private static final float LOW_EMISSIONS_LEVEL = 1000f;
    private static final float MEDIUM_EMISSIONS_LEVEL = 5000f;
    private static final float HIGH_EMISSIONS_LEVEL = 10_000f;

    /**
     * Method that listens to forge's CropGrowEvent.Pre, which fires
     * before a crop attempts to grow to its next stage
     * @param event
     */
    public static void onCropGrowth(BlockEvent.CropGrowEvent.Pre event) {
        // Getting level and BlockPos
        ServerLevel level = (ServerLevel) event.getLevel();
        BlockPos position = event.getPos();
        // Checking emissions at the block's position
        EmissionManager manager = EmissionManager.get(level);
        float emissions = manager.getEmissions(position);
        if (emissions < LOW_EMISSIONS_LEVEL) {
            // Allow the crop to grow early
            event.setResult(Event.Result.ALLOW);
        }
        else if (emissions < MEDIUM_EMISSIONS_LEVEL) {
            // Otherwise set to vanilla growth behavior
            event.setResult(Event.Result.DEFAULT);
        }
        else if (emissions < HIGH_EMISSIONS_LEVEL) {
            // Growth rate should slow down based on the emissions level
            // This scales linearly until the emissions reaches the High level and growth chance becomes <= zero
            float newGrowthChance = DEFAULT_CHANCE - (DEFAULT_CHANCE * (emissions / HIGH_EMISSIONS_LEVEL));
            if (Math.random() < newGrowthChance) {
                event.setResult(Event.Result.ALLOW);
            }
            else {
                event.setResult(Event.Result.DENY);
            }
        }
        else {
            // replace the farmland with coarse dirt
            level.setBlockAndUpdate(position.below(), Blocks.COARSE_DIRT.defaultBlockState());
        }
    }
}
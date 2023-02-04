package net.syn100.ecocraft.emissionsystem.data;


import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;

public class EmissionEvents {

    /**
     * This method is triggered when a fire block spreads to a nearby block
     * @param event When a block's neighbor is notified/updated
     */
    public static void placeFire(BlockEvent.NeighborNotifyEvent event) {
        if (event.getState().getBlock() == Blocks.FIRE) {
            EmissionManager manager = EmissionManager.get((Level) event.getLevel());
            manager.increaseEmissions(event.getPos(), 1);
        }
    }

    /**
     * This method is called every tick in order to sync the chunk's emissions level
     * to the player's client, so it can be rendered on screen.
     * @param event Event fired every tick
     */
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.level.isClientSide) {
            return;
        }
        if (event.phase == TickEvent.Phase.START) {
            return;
        }
        EmissionManager manager = EmissionManager.get(event.level);
        manager.tick(event.level);
    }
}

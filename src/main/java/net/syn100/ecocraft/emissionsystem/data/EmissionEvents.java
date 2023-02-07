package net.syn100.ecocraft.emissionsystem.data;

import net.minecraftforge.event.TickEvent;

public class EmissionEvents {
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

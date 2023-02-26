package net.syn100.ecocraft.emissionsystem.data;

import net.minecraftforge.event.TickEvent;

public class PollutionEvents {
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        // Don't do anything client side
        if (event.level.isClientSide) {
            return;
        }
        if (event.phase == TickEvent.Phase.START) {
            return;
        }

        // Get the pollution manager
        PollutionManager manager = PollutionManager.get(event.level);
        manager.tick(event.level);
    }
}

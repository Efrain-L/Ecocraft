package net.syn100.ecocraft.emissionsystem.data;


import net.minecraftforge.event.TickEvent;

public class EmissionEvents {

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

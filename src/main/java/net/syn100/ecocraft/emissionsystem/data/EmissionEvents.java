package net.syn100.ecocraft.emissionsystem.data;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.server.command.ConfigCommand;
import net.syn100.ecocraft.commands.AddEmissionCommand;

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
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        new AddEmissionCommand(event.getDispatcher());
        ConfigCommand.register(event.getDispatcher());
    }
}

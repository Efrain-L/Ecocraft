package net.syn100.ecocraft.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.syn100.ecocraft.emissionsystem.data.EmissionManager;

public class EmissionSpreadingCommand {
    public EmissionSpreadingCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("emission")
                .then(Commands.literal("spreading").then(Commands.argument("bool", BoolArgumentType.bool())
                        .executes((command) -> {
                            return emissionSpread(command.getSource(), BoolArgumentType.getBool(command, "bool"));
                        }))));
    }

    private int emissionSpread(CommandSourceStack source, boolean bool) throws CommandSyntaxException {
        ServerLevel level = source.getLevel();
        EmissionManager manager = EmissionManager.get(level);

        manager.setEmissionSpreading(bool);
        source.sendSuccess(Component.translatable("Emission Spreading is now set to " + bool), true);

        return 1;
    }
}

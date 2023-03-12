package net.syn100.ecocraft.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.syn100.ecocraft.emissionsystem.data.EmissionManager;

public class AddEmissionCommand {
    // The command should be /emission add <target value>
    public AddEmissionCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("emission")
            .then(Commands.literal("add").then(Commands.argument("targetValue", IntegerArgumentType.integer(0))
            .executes((command) -> {
                return addEmission(command.getSource(), IntegerArgumentType.getInteger(command, "targetValue"));
        }))));
    }

    private int addEmission(CommandSourceStack source, int targetValue) throws CommandSyntaxException {
        ServerLevel level = source.getLevel();
        EmissionManager manager = EmissionManager.get(level);
        Player player = source.getPlayer();
        BlockPos playerPos = player.getOnPos();
        String pos = "(" + playerPos.getX() + ", " + playerPos.getZ() + ")";

        manager.increaseEmissions(playerPos,targetValue);

        source.sendSuccess(Component.translatable("Emission added at "+ pos), true);

        return 1;
    }
}

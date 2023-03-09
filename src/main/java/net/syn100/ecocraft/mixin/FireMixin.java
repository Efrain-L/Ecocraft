package net.syn100.ecocraft.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.syn100.ecocraft.emissionsystem.data.EmissionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.lang.Math;

/**
 * Mixins basically inject new code into methods/classes from the game
 */
@Mixin(FireBlock.class)
public abstract class FireMixin {
    /**
     * This effectively injects new code into the start of the fire block's tick method.
     * This makes it so that fire will constantly be creating new carbon
     * emissions every game tick, and not just when placed.
     */
    @Inject(method = "tick", at = @At(value = "HEAD"))
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom, CallbackInfo info) {
        EmissionManager manager = EmissionManager.get(pLevel);
        // Random increase from 12-16 for kg
        manager.increaseEmissions(pPos, (float)(12+Math.random()*5));
    }
}

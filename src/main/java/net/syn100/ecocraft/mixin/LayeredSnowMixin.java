package net.syn100.ecocraft.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.syn100.ecocraft.emissionsystem.data.EmissionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.world.level.block.Block.dropResources;

@Mixin(SnowLayerBlock.class)
public abstract class LayeredSnowMixin {
    private static final int snowLayerMeltLevel = 1000;

    /**
     * Snow blocks will check the light level near them every tick, using this
     * same method to check the emissions level and remove the snow there if the
     * emissions level is higher than some set level.
     */
    @Inject(method = "randomTick", at = @At("HEAD"))
    public void randomTick(BlockState p_222448_, ServerLevel p_222449_, BlockPos p_222450_, RandomSource p_222451_, CallbackInfo info) {
        EmissionManager manager = EmissionManager.get(p_222449_);
        if (manager.getEmissions(p_222450_) > snowLayerMeltLevel) {
            // Code from the original method which will remove the snow layer
            dropResources(p_222448_, p_222449_, p_222450_);
            p_222449_.removeBlock(p_222450_, false);
        }
    }
}

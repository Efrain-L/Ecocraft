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
    private static final int snowLayerMeltLevel = 200;

    /**
     * Snow blocks will check the light level near them every tick, using this
     * same method to check the emissions level and remove the snow there if the
     * emissions level is higher than some set level.
     */
    @Inject(method = "randomTick", at = @At("HEAD"))
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom, CallbackInfo info) {
        EmissionManager manager = EmissionManager.get(pLevel);
        if (manager.getEmissions(pPos) > snowLayerMeltLevel) {
            // Code from the original method which will remove the snow layer
            dropResources(pState, pLevel, pPos);
            pLevel.removeBlock(pPos, false);
        }
    }
}

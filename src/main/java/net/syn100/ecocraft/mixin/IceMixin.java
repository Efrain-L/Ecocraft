package net.syn100.ecocraft.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.syn100.ecocraft.emissionsystem.data.EmissionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IceBlock.class)
public abstract class IceMixin {
    private static final int iceMeltLevel = 200;

    /**
     * This will call the Ice block's melt method whenever the emissions level is
     * above a certain point.
     */
    @Inject(method = "randomTick", at = @At("HEAD"))
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom, CallbackInfo info) {
        EmissionManager manager = EmissionManager.get(pLevel);
        if (manager.getEmissions(pPos) > iceMeltLevel) {
            this.melt(pState, pLevel, pPos);
        }
    }

    /**
     * This method does not actually do anything, its just so the method the Mixin
     * will inject into above can use the "this.melt" method, since the mixin needs
     * to be valid code on its own for the Java compiler.
     */
    @Shadow
    protected abstract void melt(BlockState bs, Level l, BlockPos bp);
}

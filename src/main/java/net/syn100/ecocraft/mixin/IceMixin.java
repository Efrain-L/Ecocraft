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
    public void randomTick(BlockState p_221355_, ServerLevel p_221356_, BlockPos p_221357_, RandomSource p_221358_, CallbackInfo info) {
        EmissionManager manager = EmissionManager.get(p_221356_);
        if (manager.getEmissions(p_221357_) > iceMeltLevel) {
            this.melt(p_221355_, p_221356_, p_221357_);
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

package net.syn100.ecocraft.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.syn100.ecocraft.emissionsystem.data.EmissionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(LeavesBlock.class)
public abstract class LeavesMixin {

    private static int counterForRemove = 0;

    private static int decCO2Counter = 0;

    @Inject(method = "randomTick", at = @At("HEAD"))
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom, CallbackInfo info) {
        EmissionManager manager = EmissionManager.get(pLevel);

        if (++decCO2Counter > 40 && manager.getEmissions(pPos) >= 0) {
            manager.decreaseEmissions(pPos, 1);
            decCO2Counter = 0;
        }
    }
    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom, CallbackInfo info){
    EmissionManager manager = EmissionManager.get(pLevel);

        if (++counterForRemove > 40 && decaying(pState)) {
            manager.increaseEmissions(pPos, 1);
            counterForRemove = 0;
        }
    }



    @Shadow
    protected abstract boolean decaying(BlockState b);

    /**
     * @author chenfei yan
     * @reason Because I need randomlyTicking check all the time
     */
    @Overwrite
    public boolean isRandomlyTicking(BlockState pState) {
        return true;
    }

}

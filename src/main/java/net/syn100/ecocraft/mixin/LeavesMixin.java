package net.syn100.ecocraft.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
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
    public void randomTick(BlockState p_221379_, ServerLevel p_221380_, BlockPos p_221381_, RandomSource p_221382_, CallbackInfo info) {
        EmissionManager manager = EmissionManager.get(p_221380_);

        if (++decCO2Counter > 40 && manager.getEmissions(p_221381_) >= 0) {
            manager.decreaseEmissions(p_221381_, 1);
            decCO2Counter = 0;
        }
    }
    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(BlockState p_221369_, ServerLevel p_221370_, BlockPos p_221371_, RandomSource p_221372_, CallbackInfo info){
    EmissionManager manager = EmissionManager.get(p_221370_);

        if (++counterForRemove > 40 && decaying(p_221369_)) {
            manager.increaseEmissions(p_221371_, 1);
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
    public boolean isRandomlyTicking(BlockState p_54449_) {
        return true;
    }

}

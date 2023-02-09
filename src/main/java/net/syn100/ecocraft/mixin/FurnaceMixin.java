package net.syn100.ecocraft.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.syn100.ecocraft.emissionsystem.data.EmissionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Using Mixins again, injecting into the Furnace's tick method server-side.
 * This time though, I am using another forge dependency added to the build.gradle file
 * called AccessTransformers, which will change private methods/fields to become public.
 * (You can also find the config for this in src/main/resources/META-INF/accesstransformer.cfg)
 */
@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class FurnaceMixin {
    /**
     * Using a counter to keep track of ticks, otherwise the furnace will
     * increase emissions ridiculously fast.
     */
    private static int counter = 0;
    /**
     * Injecting code into the serverTick method from the AbstractFurnaceBlockEntity class,
     * which should affect all blocks that extend it, like furnaces, smokers, and blast furnaces.
     * Used access transformer to get the .isLit() method to check if a furnace is burning something.
     */
    @Inject(method = "serverTick", at = @At("RETURN"))
    private static void serverTick(Level p_155014_, BlockPos p_155015_, BlockState p_155016_, AbstractFurnaceBlockEntity p_155017_, CallbackInfo info) {
        // Currently set to increase emissions roughly each second or so
        if ( p_155017_.isLit() && ++counter > 40) {
            EmissionManager manager = EmissionManager.get(p_155014_);
            manager.increaseEmissions(p_155015_, 1);
            // reset counter
            counter = 0;
        }
    }
}

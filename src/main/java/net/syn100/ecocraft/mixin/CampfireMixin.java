package net.syn100.ecocraft.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.syn100.ecocraft.emissionsystem.data.EmissionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CampfireBlockEntity.class)
public abstract class CampfireMixin {
    /**
     * Similar to the furnace mixin, using a counter
     */
    private static int counter = 0;

    /**
     * Injecting into this method that is called every tick when a campfire is lit
     */
    @Inject(method = "cookTick", at = @At(value = "HEAD"))
    private static void cookTick(Level p_155307_, BlockPos p_155308_, BlockState p_155309_, CampfireBlockEntity p_155310_, CallbackInfo info) {
        if (++counter > 20) {
            EmissionManager manager = EmissionManager.get(p_155307_);
            manager.increaseEmissions(p_155308_, 4.1F);
            counter = 0;
        }
    }
}

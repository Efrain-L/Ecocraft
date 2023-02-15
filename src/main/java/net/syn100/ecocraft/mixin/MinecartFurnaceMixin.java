package net.syn100.ecocraft.mixin;

import net.minecraft.world.entity.vehicle.MinecartFurnace;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.syn100.ecocraft.emissionsystem.data.EmissionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecartFurnace.class)
public abstract class MinecartFurnaceMixin extends AbstractMinecart {
    private static int counter = 0;
    private static final int EMISSIONS = 1;

    // Dummy constructor to make compiler happy: will never be called
    // Subclassing AbstractMinecart is necessary to call this.blockPosition(), this.getLevel(), etc.
    @SuppressWarnings("DataFlowIssue")
    public MinecartFurnaceMixin() {
        super(null, null);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void tick(CallbackInfo ignoredUnused) {
        if (!this.getLevel().isClientSide() && this.hasFuel() && ++counter > 40) {
            EmissionManager manager = EmissionManager.get(this.getLevel());
            manager.increaseEmissions(this.blockPosition(), EMISSIONS);
            // reset counter
            counter = 0;
        }
    }

    @Shadow
    protected abstract boolean hasFuel();
}

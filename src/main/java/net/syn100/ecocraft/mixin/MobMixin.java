package net.syn100.ecocraft.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.level.Level;
import net.syn100.ecocraft.emissionsystem.data.EmissionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity {
    private static final float DANGER_THRESHOLD = 500.0f;
    private static final float INCREMENT = 0.00001f; // increased probability of death per tick per emission unit

    @Nullable
    @Shadow
    private ResourceLocation lootTable;

    protected MobMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @SuppressWarnings("ConstantValue")
    private boolean canDieFromEmissions() {
        if (this.isInvulnerable())
            return false;
        Entity self = this;
        return (self instanceof Animal || self instanceof WaterAnimal) && !(self instanceof Hoglin);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ignoredUnused) {
        if (this.getLevel().isClientSide() || !this.canDieFromEmissions()) {
            return;
        }
        // Server side only code
        EmissionManager manager = EmissionManager.get(this.getLevel());
        float emissions = manager.getEmissions(this.blockPosition());
        if (emissions >= DANGER_THRESHOLD) {
            if (this.random.nextFloat() < (emissions - DANGER_THRESHOLD) * INCREMENT) {
                System.out.println("Killing " + this + " due to carbon emissions!");
                lootTable = new ResourceLocation("ecocraft:gameplay/emission_death");
                this.hurt(DamageSource.WITHER, Float.MAX_VALUE);
            }
        }
    }
}

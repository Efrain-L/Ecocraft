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
import java.util.Map;

import javax.annotation.Nullable;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity {
    private static final float DANGER_THRESHOLD = 500.0f;
    private static final float INCREMENT = 0.00001f; // increased probability of death per tick per emission unit

    // Emissions produced by a mob per second in kg.
    // To add an entity, use the ID and the emissions value as an entry.
    private static final Map<String, Float> EMISSIONS_VALUES = Map.ofEntries(
            Map.entry("minecraft:cow", 0.0001044F),
            Map.entry("minecraft:pig",0.0001762F),
            Map.entry("minecraft:sheep", 0.00001434F)
    );

    private static final float DEFAULT_EMISSIONS_VALUE = 0;

    @Nullable
    @Shadow
    private ResourceLocation lootTable;

    protected MobMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @SuppressWarnings("ConstantValue")
    private boolean canDieFromEmissions() {
        if (this.isInvulnerable())
            return false;
        Entity self = this;
        return (self instanceof Animal || self instanceof WaterAnimal) && !(self instanceof Hoglin);
    }

    /**
     * Gets the emissions produced by an entity if it is in the map
     * @return Emissions of an entity in kg/seconds
     */
    private float emissionsProduced(){
        if(EMISSIONS_VALUES.containsKey(this.getEncodeId())){
            return EMISSIONS_VALUES.get(this.getEncodeId());
        }
        return DEFAULT_EMISSIONS_VALUE;
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

        // Emissions increase for mobs,
        // with emissions rate divided by 20 to account for ticks.
        manager.increaseEmissions(this.blockPosition(), emissionsProduced()/20);
    }
}

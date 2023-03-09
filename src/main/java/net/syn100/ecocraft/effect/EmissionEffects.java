package net.syn100.ecocraft.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.syn100.ecocraft.emissionsystem.data.EmissionManager;

import java.util.List;

public class EmissionEffects {
    private static final int EFFECT_DURATION = 100;
    private static final int EFFECT_AMPLIFIER = 0;
    private static final boolean EFFECT_AMBIENT = false;
    private static final boolean EFFECT_VISIBLE = false;

    private static final float GOOD_AIR_QUALITY_THRESHOLD = 0f;
    private static final float LOW_AIR_QUALITY_THRESHOLD = 100f;
    private static final float POOR_AIR_QUALITY_THRESHOLD = 500f;
    private static final float TERRIBLE_AIR_QUALITY_THRESHOLD = 750f;
    private static final float TOXIC_AIR_THRESHOLD = 1000f;

    /**
     * This method is meant to be used elsewhere, and will call each of the
     * individual effect update methods for positive and negative effects on mobs
     * and/or players
     * @param level - The world's server level
     */
    public static void UpdateEffects(ServerLevel level) {
        UpdateNegativeMobEffects(level);
        UpdatePositiveMobEffects(level);
        UpdateNegativePlayerEffects(level);
        UpdatePositivePlayerEffects(level);
    }

    public static void UpdateAshEffects(ServerLevel level, BlockPos position) {
        EmissionManager manager = EmissionManager.get(level);
        float emissions = manager.getEmissions(position);
        if (emissions > TOXIC_AIR_THRESHOLD) {
            level.sendParticles(ParticleTypes.ASH,
                            position.getX(), 70, position.getZ(),
                            50, 2, 64, 2, 1);
        }
    }

    /**
     * Will apply negative effects to ALL mobs based on
     * the emission level thresholds
     * @param level - The world's server level
     */
    private static void UpdateNegativeMobEffects(ServerLevel level) {
        if (!level.isClientSide()) {
            // Loop through each Living entity (mob)
            Iterable<Entity> mobs = level.getEntities().getAll();
            mobs.forEach((entity) -> {
                if (entity instanceof LivingEntity) {
                    EmissionManager manager = EmissionManager.get(level);
                    float emissions = manager.getEmissions(entity.getOnPos());
                    // Apply negative effects
                    if (emissions > TOXIC_AIR_THRESHOLD) {
                        ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.WITHER, EFFECT_DURATION, 1+EFFECT_AMPLIFIER, EFFECT_AMBIENT, EFFECT_VISIBLE));
                        ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.DARKNESS, EFFECT_DURATION, EFFECT_AMPLIFIER, EFFECT_AMBIENT, EFFECT_VISIBLE));
                    }
                }
            });
        }
    }

    /**
     * Will apply positive effects to ALL mobs based on
     * the emission level thresholds
     * @param level - The world's server level
     */
    private static void UpdatePositiveMobEffects(ServerLevel level) {
        if (!level.isClientSide()) {

        }
    }

    /**
     * Applies negative effects ONLY to the players in the game
     * @param level - The world's server level
     */
    private static void UpdateNegativePlayerEffects(ServerLevel level) {
        if (!level.isClientSide()) {
            List<ServerPlayer> players = level.players();
            players.forEach((player) -> {
                EmissionManager manager = EmissionManager.get(level);
                float emissions = manager.getEmissions(player.getOnPos());
                if (emissions > TERRIBLE_AIR_QUALITY_THRESHOLD) {
                    player.addEffect(new MobEffectInstance(MobEffects.UNLUCK, EFFECT_DURATION, EFFECT_AMPLIFIER, EFFECT_AMBIENT, EFFECT_VISIBLE));
                }
                if (emissions > POOR_AIR_QUALITY_THRESHOLD) {
                    player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, EFFECT_DURATION, EFFECT_AMPLIFIER, EFFECT_AMBIENT, EFFECT_VISIBLE));
                }
                if (emissions > LOW_AIR_QUALITY_THRESHOLD) {
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, EFFECT_DURATION, EFFECT_AMPLIFIER, EFFECT_AMBIENT, EFFECT_VISIBLE));
                }
            });
        }
    }

    /**
     * Applies positive effects ONLY to the players in the game
     * @param level - The world's server level
     */
    private static void UpdatePositivePlayerEffects(ServerLevel level) {
        if (!level.isClientSide()) {
            List<ServerPlayer> players = level.players();
            players.forEach((player) -> {
                EmissionManager manager = EmissionManager.get(level);
                float emissions = manager.getEmissions(player.getOnPos());
                if (emissions < GOOD_AIR_QUALITY_THRESHOLD) {
                    player.addEffect(new MobEffectInstance(MobEffects.LUCK, EFFECT_DURATION, EFFECT_AMPLIFIER, EFFECT_AMBIENT, EFFECT_VISIBLE));
                }
            });
        }
    }
}
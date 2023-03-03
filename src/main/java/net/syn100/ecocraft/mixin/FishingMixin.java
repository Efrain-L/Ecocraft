package net.syn100.ecocraft.mixin;


import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.Collections;
import java.util.List;

import net.syn100.ecocraft.emissionsystem.data.EmissionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;

import javax.annotation.Nullable;

@Mixin(FishingHook.class)
public abstract class FishingMixin extends Projectile {

    private static final float DANGER_THRESHOLD = 500.0f;
    protected FishingMixin(EntityType<? extends Projectile> p_37248_, Level p_37249_) {
        super(p_37248_, p_37249_);
    }

    @Shadow
    protected abstract Player getPlayerOwner();

    @Shadow
    protected abstract boolean shouldStopFishing(Player player);

    @Shadow
    private Entity hookedIn;

    @Shadow
    protected abstract void pullEntity(Entity p_150156_);
    @Nullable
    @Shadow
    private int luck;
    @Shadow
    private int nibble;



    /**
     * @author
     * @reason
     */
    @Overwrite
    public int retrieve(ItemStack p_37157_) {
        Player player = this.getPlayerOwner();
        if (!this.level.isClientSide && player != null && !this.shouldStopFishing(player)) {
            int i = 0;
            net.minecraftforge.event.entity.player.ItemFishedEvent event = null;
            if (this.hookedIn != null) {
                this.pullEntity(this.hookedIn);
                CriteriaTriggers.FISHING_ROD_HOOKED.trigger((ServerPlayer)player, p_37157_, (FishingHook)(Object)this, Collections.emptyList());
                this.level.broadcastEntityEvent((FishingHook)(Object)this, (byte)31);
                i = this.hookedIn instanceof ItemEntity ? 3 : 5;
            } else if (this.nibble > 0) {
                LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerLevel)this.level)).withParameter(LootContextParams.ORIGIN, this.position()).withParameter(LootContextParams.TOOL, p_37157_).withParameter(LootContextParams.THIS_ENTITY, this).withRandom(this.random).withLuck((float)this.luck + player.getLuck());
                lootcontext$builder.withParameter(LootContextParams.KILLER_ENTITY, this.getOwner()).withParameter(LootContextParams.THIS_ENTITY, (FishingHook)(Object)this);

                EmissionManager manager = EmissionManager.get(this.getLevel());
                float emissions = manager.getEmissions(this.blockPosition());

                LootTable loottable;
                int rand = random.nextInt(100);
                if (emissions >= DANGER_THRESHOLD) {
                    if (rand < 50) {
                        loottable = this.level.getServer().getLootTables().get(BuiltInLootTables.FISHING_JUNK);
                    } else if (rand < 99) {
                        loottable = this.level.getServer().getLootTables().get(BuiltInLootTables.FISHING_FISH);
                    } else {
                        loottable = this.level.getServer().getLootTables().get(BuiltInLootTables.FISHING_TREASURE);

                    }
                } else {
                    if (rand < 5) {
                        loottable = this.level.getServer().getLootTables().get(BuiltInLootTables.FISHING_JUNK);
                    } else if (rand < 95) {
                        loottable = this.level.getServer().getLootTables().get(BuiltInLootTables.FISHING_FISH);
                    } else {
                        loottable = this.level.getServer().getLootTables().get(BuiltInLootTables.FISHING_TREASURE);
                    }
                }

                List<ItemStack> list = loottable.getRandomItems(lootcontext$builder.create(LootContextParamSets.FISHING));
                event = new net.minecraftforge.event.entity.player.ItemFishedEvent(list, this.onGround ? 2 : 1, (FishingHook)(Object)this);
                net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
                if (event.isCanceled()) {
                    this.discard();
                    return event.getRodDamage();
                }
                CriteriaTriggers.FISHING_ROD_HOOKED.trigger((ServerPlayer)player, p_37157_, (FishingHook)(Object)this, list);

                for(ItemStack itemstack : list) {
                    ItemEntity itementity = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), itemstack);
                    double d0 = player.getX() - this.getX();
                    double d1 = player.getY() - this.getY();
                    double d2 = player.getZ() - this.getZ();
                    double d3 = 0.1D;
                    itementity.setDeltaMovement(d0 * 0.1D, d1 * 0.1D + Math.sqrt(Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2)) * 0.08D, d2 * 0.1D);
                    this.level.addFreshEntity(itementity);
                    player.level.addFreshEntity(new ExperienceOrb(player.level, player.getX(), player.getY() + 0.5D, player.getZ() + 0.5D, this.random.nextInt(6) + 1));
                    if (itemstack.is(ItemTags.FISHES)) {
                        player.awardStat(Stats.FISH_CAUGHT, 1);
                    }
                }

                i = 1;
            }

            if (this.onGround) {
                i = 2;
            }

            this.discard();
            return event == null ? i : event.getRodDamage();
        } else {
            return 0;
        }
    }

}
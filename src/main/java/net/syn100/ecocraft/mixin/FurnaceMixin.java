package net.syn100.ecocraft.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import net.syn100.ecocraft.IFurnaceMixin;
import net.syn100.ecocraft.emissionsystem.data.EmissionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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
@SuppressWarnings("unused")
public abstract class FurnaceMixin implements IFurnaceMixin {
    private static final int DEFAULT_EMISSIONS = 1;
    private static final String BURNING_ITEM_TAG = "EcocraftCurrentFuel";

    private static Item prevFuelItem;
    private static int prevFuelCount;

    private Item currentFuel;
    @Shadow
    int litTime;

    @Override
    public int getLitTime() {
        return litTime;
    }

    @Override
    public Item getCurrentFuel() {
        return currentFuel;
    }

    @Override
    public void setCurrentFuel(Item currentFuel) {
        this.currentFuel = currentFuel;
    }

    private static int getEmissions(Item item) {
        if (item == Items.COAL) {
            return 1;
        } else if (item == Items.CHARCOAL) {
            return 2;
        } else if (item == Items.LAVA_BUCKET) {
            return 0;
        } else {
            return DEFAULT_EMISSIONS;
        }
    }

    @Inject(method = "load", at = @At("RETURN"))
    public void load(CompoundTag tag, CallbackInfo info) {
        String fuelItemName = tag.getString(BURNING_ITEM_TAG);
        System.out.println("Furnace load: " + fuelItemName);
        if (fuelItemName.isEmpty()) {
            fuelItemName = "minecraft:air";
        }
        this.currentFuel = ForgeRegistries.ITEMS.getValue(new ResourceLocation(fuelItemName));
        if (this.currentFuel == null) {
            this.currentFuel = Items.AIR;
        }
    }

    @Inject(method = "saveAdditional", at = @At("RETURN"))
    protected void saveAdditional(CompoundTag tag, CallbackInfo info) {
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(this.currentFuel);
        System.out.println("Furnace save: " + key);
        tag.putString(BURNING_ITEM_TAG, key == null ? "minecraft:air" : key.toString());
    }

    @Inject(method = "serverTick", at = @At("HEAD"))
    private static void serverTickHead(Level p_155014_, BlockPos p_155015_, BlockState p_155016_, AbstractFurnaceBlockEntity p_155017_, CallbackInfo info) {
        ItemStack fuelStack = p_155017_.getItem(1);
        prevFuelItem = fuelStack.getItem();
        prevFuelCount = fuelStack.getCount();
    }

    /**
     * Injecting code into the serverTick method from the AbstractFurnaceBlockEntity class,
     * which should affect all blocks that extend it, like furnaces, smokers, and blast furnaces.
     * Used access transformer to get the .isLit() method to check if a furnace is burning something.
     */
    @Inject(method = "serverTick", at = @At("RETURN"))
    private static void serverTickReturn(Level p_155014_, BlockPos p_155015_, BlockState p_155016_, AbstractFurnaceBlockEntity abstractFurnace, CallbackInfo info) {
        IFurnaceMixin furnace = (IFurnaceMixin) abstractFurnace;
        // Check whether fuel changes
        ItemStack fuelStack = abstractFurnace.getItem(1);
        Item currentFuelItem = fuelStack.getItem();
        int currentFuelCount = fuelStack.getCount();
        // Check for changes in the fuel slot and assume fuel is consumed
        if ((currentFuelItem != prevFuelItem || currentFuelCount != prevFuelCount) && prevFuelItem != Items.BUCKET) {
            // Note that `prevFuelItem != Items.BUCKET`
            // is to deal with special case of bucket -> water bucket upon drying wet sponge which is not a fuel usage
            System.out.println("Fuel changed:");
            System.out.println("Before: " + prevFuelItem + " * " + prevFuelCount);
            System.out.println("After: " + currentFuelItem + " * " + currentFuelCount);
            furnace.setCurrentFuel(prevFuelItem);
        }
        // Currently set to increase emissions roughly each second or so
        // use litTime modulo 40 to get a rough per-furnace counter that fires every 40 ticks
        if ( abstractFurnace.isLit() && furnace.getLitTime() % 40 == 0) {
            System.out.println("Increasing emissions for fuel: " + furnace.getCurrentFuel());
            EmissionManager manager = EmissionManager.get(p_155014_);
            manager.increaseEmissions(p_155015_, getEmissions(furnace.getCurrentFuel()));
        }
    }
}

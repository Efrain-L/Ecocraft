package net.syn100.ecocraft.setup;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.syn100.ecocraft.EcoCraft;

public class Registration {
    //Object to be used for registering custom blocks
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, EcoCraft.MOD_ID);

    // This method will actually register the blocks and items
    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    }

}

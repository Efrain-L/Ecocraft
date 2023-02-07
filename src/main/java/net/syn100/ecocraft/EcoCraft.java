package net.syn100.ecocraft;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.syn100.ecocraft.setup.*;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(EcoCraft.MOD_ID)
public class EcoCraft {
    public static final String MOD_ID = "ecocraft";

    public EcoCraft() {
        // running individual setup methods
        ModSetup.setup();
        Registration.init();
        Config.register();
        // Setting up event bus
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(ModSetup::init);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modEventBus.addListener(ClientSetup::init));
    }
}

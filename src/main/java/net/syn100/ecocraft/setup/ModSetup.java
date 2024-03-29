package net.syn100.ecocraft.setup;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.syn100.ecocraft.EcoCraft;
import net.syn100.ecocraft.emissionsystem.data.EmissionEvents;
import net.syn100.ecocraft.emissionsystem.data.PollutionEvents;
import net.syn100.ecocraft.events.CropGrowingEvents;

@Mod.EventBusSubscriber(modid = EcoCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSetup {
    public static void setup() {
        IEventBus bus = MinecraftForge.EVENT_BUS;
        bus.addListener(EmissionEvents::onWorldTick);
        bus.addListener(EmissionEvents::onCommandsRegister);
        bus.addListener(PollutionEvents::onWorldTick);
        bus.addListener(CropGrowingEvents::onCropGrowth);
    }

    public static void init(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Messages.register();
        });
    }
}

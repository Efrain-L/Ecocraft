package net.syn100.ecocraft.setup;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.syn100.ecocraft.EcoCraft;
import net.syn100.ecocraft.emissionsystem.client.EmissionsOverlay;
import net.syn100.ecocraft.emissionsystem.client.PollutionsOverlay;

@Mod.EventBusSubscriber(modid = EcoCraft.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {
        public static void init(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {

            });
        }

        @SubscribeEvent
        public static void onRegisterOverlays(RegisterGuiOverlaysEvent event) {
            event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "emissions_overlay", EmissionsOverlay.HUD_EMISSIONS);
            event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "pollutions_overlay", PollutionsOverlay.HUD_POLLUTIONS);
        }
}
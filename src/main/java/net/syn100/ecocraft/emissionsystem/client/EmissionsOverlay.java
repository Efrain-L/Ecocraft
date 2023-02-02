package net.syn100.ecocraft.emissionsystem.client;

import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.syn100.ecocraft.emissionsystem.EmissionConfig;

public class EmissionsOverlay {
    public static final IGuiOverlay HUD_EMISSIONS = (gui, poseStack, partialTicks, width, height) -> {
        String toDisplay = "Emissions: " + ClientEmissionData.getChunkEmissions();
        int x = EmissionConfig.EMISSIONS_HUD_X.get();
        int y = EmissionConfig.EMISSIONS_HUD_Y.get();
        gui.getFont().draw(poseStack, toDisplay, x, y, EmissionConfig.EMISSIONS_HUD_COLOR.get());
    };
}

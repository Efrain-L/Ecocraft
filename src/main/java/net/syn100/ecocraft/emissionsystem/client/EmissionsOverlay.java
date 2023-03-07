package net.syn100.ecocraft.emissionsystem.client;

import net.minecraft.util.FastColor;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.syn100.ecocraft.emissionsystem.EmissionConfig;

public class EmissionsOverlay {
    public static final IGuiOverlay HUD_EMISSIONS = (gui, poseStack, partialTicks, width, height) -> {
        float currentEmission = ClientEmissionData.getChunkEmissions();
        String toDisplay = "Emissions: " + String.format("%.2f", currentEmission) + " kg";
        int x = EmissionConfig.EMISSIONS_HUD_X.get();
        int y = EmissionConfig.EMISSIONS_HUD_Y.get();
        int color = EmissionConfig.EMISSIONS_HUD_COLOR.get();
        int THRESHOLD_LOW = 0;      // emission level at which the color being most green
        int THRESHOLD_MEDIUM = 64;  // emission level at which the color being most yellow
        int THRESHOLD_HIGH = 128;   // emission level at which the color being most red
        int RGB_MAX = 255;

        if(color == 0xffffffff) { // only be effective when EMISSIONS_HUD_COLOR is in default value
            // set the gui color according to the emission level (green if low, yellow if medium, red if high)
            // arguments for the color method: alpha, R, G, B
            int alpha = 0;
            int r = 0;
            int g = 0;
            int b = 0;

            // gradually increase the R value (from green to yellow)
            if(currentEmission <= THRESHOLD_MEDIUM) {
                r = Math.round((currentEmission - THRESHOLD_LOW)/(THRESHOLD_MEDIUM - THRESHOLD_LOW) * RGB_MAX);
                g = RGB_MAX;
            // gradually decrease the G value (from yellow to red)
            } else if (currentEmission <= THRESHOLD_HIGH) {
                r = RGB_MAX;
                g = Math.round((1-(currentEmission - THRESHOLD_MEDIUM)/(THRESHOLD_HIGH - THRESHOLD_MEDIUM)) * RGB_MAX);
            // red
            } else {
                r = RGB_MAX;
                g = 0;
            }

            // transform into RGB value
            color = FastColor.ARGB32.color(alpha,r,g,b);
        }
        gui.getFont().draw(poseStack, toDisplay, x, y, color);
    };
}

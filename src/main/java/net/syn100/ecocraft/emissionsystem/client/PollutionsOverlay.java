package net.syn100.ecocraft.emissionsystem.client;

import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.syn100.ecocraft.emissionsystem.PollutionConfig;

public class PollutionsOverlay {
    public static final IGuiOverlay HUD_POLLUTIONS = (gui, poseStack, partialTicks, width, height) -> {
        String toDisplay = "Pollutions: " + ClientPollutionData.getChunkPollutions();
        int x = PollutionConfig.POLLUTIONS_HUD_X.get();
        int y = PollutionConfig.POLLUTIONS_HUD_Y.get();
        gui.getFont().draw(poseStack, toDisplay, x, y, PollutionConfig.POLLUTIONS_HUD_COLOR.get());
    };
}

package net.syn100.ecocraft.emissionsystem;

import net.minecraftforge.common.ForgeConfigSpec;
public class PollutionConfig {
    public static ForgeConfigSpec.IntValue CHUNK_MIN_POLLUTIONS;
    public static ForgeConfigSpec.IntValue CHUNK_MAX_POLLUTIONS;
    public static ForgeConfigSpec.IntValue POLLUTIONS_HUD_X;
    public static ForgeConfigSpec.IntValue POLLUTIONS_HUD_Y;
    public static ForgeConfigSpec.IntValue POLLUTIONS_HUD_COLOR;

    public static void registerServerConfig(ForgeConfigSpec.Builder SERVER_BUILDER) {
        SERVER_BUILDER.comment("Settings for the pollution system").push("pollutions");

        CHUNK_MIN_POLLUTIONS = SERVER_BUILDER
                .comment("Minumum amount of pollution in a chunk")
                .defineInRange("minPollutions", 0, 0, Integer.MAX_VALUE);
        CHUNK_MAX_POLLUTIONS = SERVER_BUILDER
                .comment("Maximum amount of pollution in a chunk (relative to minPollutions)")
                .defineInRange("maxPollutions", 100, 1, Integer.MAX_VALUE);

        SERVER_BUILDER.pop();
    }

    public static void registerClientConfig(ForgeConfigSpec.Builder CLIENT_BUILDER) {
        CLIENT_BUILDER.comment("Settings for the pollution system").push("pollutions");

        POLLUTIONS_HUD_X = CLIENT_BUILDER
                .comment("X location of the pollution hud")
                .defineInRange("pollutionsHudX", 10, -1, Integer.MAX_VALUE);
        POLLUTIONS_HUD_Y = CLIENT_BUILDER
                .comment("Y location of the pollution hud")
                .defineInRange("pollutionsHudY", 20, -1, Integer.MAX_VALUE);
        POLLUTIONS_HUD_COLOR = CLIENT_BUILDER
                .comment("Color of the pollution hud")
                .defineInRange("pollutionsHudColor", 0xffffffff, Integer.MIN_VALUE, Integer.MAX_VALUE);

        CLIENT_BUILDER.pop();
    }
}

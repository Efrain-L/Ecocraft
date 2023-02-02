package net.syn100.ecocraft.emissionsystem;

import net.minecraftforge.common.ForgeConfigSpec;

public class EmissionConfig {
    public static ForgeConfigSpec.IntValue CHUNK_MIN_EMISSIONS;
    public static ForgeConfigSpec.IntValue CHUNK_MAX_EMISSIONS;
    public static ForgeConfigSpec.IntValue EMISSIONS_HUD_X;
    public static ForgeConfigSpec.IntValue EMISSIONS_HUD_Y;
    public static ForgeConfigSpec.IntValue EMISSIONS_HUD_COLOR;

    public static void registerServerConfig(ForgeConfigSpec.Builder SERVER_BUILDER) {
        SERVER_BUILDER.comment("Settings for the emissions System").push("emissions");

        CHUNK_MIN_EMISSIONS = SERVER_BUILDER
                .comment("Minimum Amount of emissions in a chunk")
                .defineInRange("minEmissions", 10, 0, Integer.MAX_VALUE);
        CHUNK_MAX_EMISSIONS = SERVER_BUILDER
                .comment("Maximum Amount of emissions in a chunk (relative to minEmissions)")
                .defineInRange("maxEmissions", 100, 1, Integer.MAX_VALUE);

        SERVER_BUILDER.pop();
    }


    public static void registerClientConfig(ForgeConfigSpec.Builder CLIENT_BUILDER) {
        CLIENT_BUILDER.comment("Settings for the emissions system").push("emissions");

        EMISSIONS_HUD_X = CLIENT_BUILDER
                .comment("X location of the Emissions HUD")
                .defineInRange("emissionsHudX", 10, -1, Integer.MAX_VALUE);
        EMISSIONS_HUD_Y = CLIENT_BUILDER
                .comment("Y location of the Emissions HUD")
                .defineInRange("emissionsHudY", 10, -1, Integer.MAX_VALUE);
        EMISSIONS_HUD_COLOR = CLIENT_BUILDER
                .comment("Color of the Emissions HUD")
                .defineInRange("emissionsHudColor", 0xffffffff, Integer.MIN_VALUE, Integer.MAX_VALUE);

        CLIENT_BUILDER.pop();
    }
}

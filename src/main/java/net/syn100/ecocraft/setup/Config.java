package net.syn100.ecocraft.setup;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.syn100.ecocraft.emissionsystem.EmissionConfig;
import net.syn100.ecocraft.emissionsystem.PollutionConfig;

public class Config {
    public static void register() {
        registerServerConfigs();
        registerClientConfigs();
    }
    public static void registerClientConfigs() {
        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
        EmissionConfig.registerClientConfig(CLIENT_BUILDER);
        PollutionConfig.registerClientConfig(CLIENT_BUILDER);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_BUILDER.build());
    }

    public static void registerServerConfigs() {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
        EmissionConfig.registerServerConfig(SERVER_BUILDER);
        PollutionConfig.registerServerConfig(SERVER_BUILDER);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_BUILDER.build());
    }
}

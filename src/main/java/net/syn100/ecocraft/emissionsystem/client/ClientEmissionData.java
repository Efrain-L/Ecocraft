package net.syn100.ecocraft.emissionsystem.client;

public class ClientEmissionData {
    private static float chunkEmissions;

    public static void set(float emissions) {
        ClientEmissionData.chunkEmissions = emissions;
    }

    public static float getChunkEmissions() { return chunkEmissions; }

}

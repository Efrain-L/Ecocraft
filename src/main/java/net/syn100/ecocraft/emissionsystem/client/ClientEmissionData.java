package net.syn100.ecocraft.emissionsystem.client;

public class ClientEmissionData {
    private static int chunkEmissions;

    public static void set(int emissions) {
        ClientEmissionData.chunkEmissions = emissions;
    }

    public static int getChunkEmissions() { return chunkEmissions; }

}

package net.syn100.ecocraft.emissionsystem.client;

public class ClientPollutionData {
    private static float chunkPollutions;

    public static void set(float chunkPollutions) {
        ClientPollutionData.chunkPollutions = chunkPollutions;
    }

    public static float getChunkPollutions() {
        return chunkPollutions;
    }
}

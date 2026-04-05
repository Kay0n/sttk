package online.refract.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import online.refract.network.S2CPackets;

public class ClientPacketReceiver {
    
    public static void register(ClientCoordinator clientCoordinator) {
        ClientPlayNetworking.registerGlobalReceiver(
            S2CPackets.SyncStatePayload.ID,
            (payload, context) -> {
                clientCoordinator.onRecieveState(payload.state());
            }
        );
        ClientPlayNetworking.registerGlobalReceiver(
            S2CPackets.ShowRoleAnimationPayload.ID,
            (payload, context) -> {
                clientCoordinator.playShowRoleAnimation();
            }
        );
            ClientPlayNetworking.registerGlobalReceiver(
                S2CPackets.AssetResponsePayload.ID,
                (payload, context) -> {
                    clientCoordinator.onAssetChunkReceived(
                        payload.assetUrl(),
                        payload.chunkIndex(),
                        payload.totalChunks(),
                        payload.chunkData()
                    );
                }
            );
    }
    
}

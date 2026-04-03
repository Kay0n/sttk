package online.refract.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import online.refract.Sttk;
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
            S2CPackets.ShowRoleAnimationPacket.ID,
            (payload, context) -> {
                clientCoordinator.playShowRoleAnimation();
            }
        );
    }
    
}

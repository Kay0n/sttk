package online.refract.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import online.refract.network.S2CPackets;

public class ClientPacketReceiver {
    
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(
            S2CPackets.SyncStatePayload.ID,
            (payload, context) -> {
                ClocktowerClientState.onRecieveState(payload.state());
            }
        );
    }
}

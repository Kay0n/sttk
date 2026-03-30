package online.refract.game.server;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import online.refract.http.TownConnectionHandler;
import online.refract.network.C2SPackets;

public class ServerPacketReceiver {
    
    public static void register(TownConnectionHandler townConnectionHandler, ClocktowerServerStateManager stateManager) {
        // Toggle Vote
        ServerPlayNetworking.registerGlobalReceiver(
            C2SPackets.ToggleVotePayload.ID,
            (payload, context) -> {
                stateManager.updateVoteActive(payload.active());
            }
        );
        
        // Link Username
        ServerPlayNetworking.registerGlobalReceiver(
            C2SPackets.LinkUsernamePayload.ID,
            (payload, context) -> {
                    stateManager.handlePacketUpdate(
                        payload.playerToLink().name(),
                        payload.minecraftUsername()
                    );
            }
        );
        
        // Connect to Town
        ServerPlayNetworking.registerGlobalReceiver(
            C2SPackets.ConnectToTownPayload.ID,
            (payload, context) -> {
                townConnectionHandler.connect(payload.townName());
            }
        );


        // Disconnect from Town
        ServerPlayNetworking.registerGlobalReceiver(
            C2SPackets.DisconnectFromTownPayload.ID,
            (payload, context) -> {
                townConnectionHandler.disconnect();
            }
        );

    }
}

package online.refract.game.server;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import online.refract.http.TownConnectionHandler;
import online.refract.network.C2SPackets;

public class ServerPacketReceiver {
    
    public static void register(TownConnectionHandler townConnectionHandler, ClocktowerServerStateManager stateManager, ScoreboardManager scoreboardManager) {

        ServerPlayNetworking.registerGlobalReceiver(
            C2SPackets.StartVoteForPlayerPayload.ID,
            (payload, context) -> {
                stateManager.updateState(stateManager.getState().withVoteActive(true));
                scoreboardManager.startVoteForPlayer(payload.player(), stateManager.getState());
            }
        );

        ServerPlayNetworking.registerGlobalReceiver(
            C2SPackets.StopVotePayload.ID,
            (payload, context) -> {
                stateManager.updateState(stateManager.getState().withVoteActive(false));
                scoreboardManager.stopVote();
            }
        );

        ServerPlayNetworking.registerGlobalReceiver(
            C2SPackets.DistributeRolesToTownPayload.ID,
            (payload, context) -> {
                // TODO: animations on the client 
            }
        );


        ServerPlayNetworking.registerGlobalReceiver(
            C2SPackets.RequestPrivateChatPayload.ID,
            (payload, context) -> {
                scoreboardManager.requestPrivateChat(payload.player());
            }
        );

        ServerPlayNetworking.registerGlobalReceiver(
            C2SPackets.RequestTeleportToPlayerPayload.ID,
            (payload, context) -> {
                scoreboardManager.requestTeleportToPlayer(payload.player());
            }
        );

        ServerPlayNetworking.registerGlobalReceiver(
            C2SPackets.RequestTeleportToHousePayload.ID,
            (payload, context) -> {
                scoreboardManager.requestTeleportToHouse(payload.player());
            }
        );
        
        ServerPlayNetworking.registerGlobalReceiver(
            C2SPackets.LinkUsernamePayload.ID,
            (payload, context) -> {
                    stateManager.updatePlayerLink(
                        payload.playerToLink().name(),
                        payload.minecraftUsername()
                    );
            }
        );
        
        ServerPlayNetworking.registerGlobalReceiver(
            C2SPackets.ConnectToTownPayload.ID,
            (payload, context) -> {
                townConnectionHandler.connect(payload.townName());
            }
        );

        ServerPlayNetworking.registerGlobalReceiver(
            C2SPackets.DisconnectFromTownPayload.ID,
            (payload, context) -> {
                townConnectionHandler.disconnect();
            }
        );

    }
}

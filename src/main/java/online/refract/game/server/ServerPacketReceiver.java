package online.refract.game.server;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import online.refract.network.C2SPackets;

public class ServerPacketReceiver {
    
    public static void register(ServerCoordinator coordinator) {

        ServerPlayNetworking.registerGlobalReceiver(
            C2SPackets.StartVoteForPlayerPayload.ID,
            (payload, context) -> {
                coordinator.startVoteForPlayer(payload.player());
            }
        );

        ServerPlayNetworking.registerGlobalReceiver(
            C2SPackets.StopVotePayload.ID,
            (payload, context) -> {
                coordinator.stopVote();
            }
        );

        ServerPlayNetworking.registerGlobalReceiver(
            C2SPackets.DistributeRolesToTownPayload.ID,
            (payload, context) -> {
                coordinator.distributeRolesToTown();
            }
        );


        ServerPlayNetworking.registerGlobalReceiver(
            C2SPackets.RequestPrivateChatPayload.ID,
            (payload, context) -> {
                coordinator.requestPrivateChat(payload.player());
            }
        );

        ServerPlayNetworking.registerGlobalReceiver(
            C2SPackets.RequestTeleportToPlayerPayload.ID,
            (payload, context) -> {
                coordinator.requestTeleportToPlayer(payload.player());
            }
        );

        ServerPlayNetworking.registerGlobalReceiver(
            C2SPackets.RequestTeleportToHousePayload.ID,
            (payload, context) -> {
                coordinator.requestTeleportToHouse(payload.player());
            }
        );
        
        ServerPlayNetworking.registerGlobalReceiver(
            C2SPackets.LinkUsernamePayload.ID,
            (payload, context) -> {
                    coordinator.updatePlayerLink(
                        payload.playerToLink().name(),
                        payload.minecraftUsername()
                    );
            }
        );
        
        ServerPlayNetworking.registerGlobalReceiver(
            C2SPackets.ConnectToTownPayload.ID,
            (payload, context) -> {
                coordinator.connectToTown(payload.townName());
            }
        );

        ServerPlayNetworking.registerGlobalReceiver(
            C2SPackets.DisconnectFromTownPayload.ID,
            (payload, context) -> {
                coordinator.disconnectFromTown();
            }
        );

            ServerPlayNetworking.registerGlobalReceiver(
                C2SPackets.AssetRequestPayload.ID,
                (payload, context) -> {
                    coordinator.sendAssetsToClient(context.player(), payload.assetUrls());            
                }
            );

    }
}

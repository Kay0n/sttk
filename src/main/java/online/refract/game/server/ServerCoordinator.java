package online.refract.game.server;

import online.refract.network.S2CPackets.ShowRoleAnimationPacket;
import online.refract.network.S2CPackets.SyncStatePayload;
import online.refract.Sttk;
import online.refract.game.state.ClocktowerPlayer;
import online.refract.game.state.ClocktowerState;
import online.refract.game.state.Enums.TownConnectionStatus;
import online.refract.http.ClocktowerStateConverter;
import online.refract.http.TownConnectionHandler;
import online.refract.http.TownConnectionHandler.ConnectionEvent;
import online.refract.http.TownConnectionHandler.ConnectionEvent.DataReceived;
import online.refract.http.TownConnectionHandler.ConnectionEvent.StatusChanged;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.io.IOException;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;


public class ServerCoordinator {


    private ClocktowerState state = ClocktowerState.EMPTY;
    private MinecraftServer server;
    private ScoreboardManager scoreboardManager;
    private TownConnectionHandler townConnectionHandler;

    public ServerCoordinator(MinecraftServer server, ScoreboardManager scoreboardManager, TownConnectionHandler townConnectionHandler) {
        this.server = server;
        this.scoreboardManager = scoreboardManager;
        this.townConnectionHandler = townConnectionHandler;
        this.townConnectionHandler.setConnectionListener(event -> this.onSSEEvent(event));
    }

    public void updateVoteActive(boolean active) {
        state = state.withVoteActive(active);
        syncState();
    }

    public void updateConnectionStatus(TownConnectionStatus newStatus) {
        this.state = state.withTownConnectionStatus(newStatus);
        syncState();
    }

    public void updatePlayerLink(String clocktowerPlayerName, String linkedMinecraftUsername) {
        state = state.withUpdatedPlayer(
            clocktowerPlayerName,
            player -> player.withLinkedMinecraftUsername(linkedMinecraftUsername)
        );
        syncState();
    }

    public void startVoteForPlayer(ClocktowerPlayer player) {
        state = state.withVoteActive(true);
        syncState();
        scoreboardManager.startVoteForPlayer(player, state);
    }

    public void stopVote() {
        state = state.withVoteActive(false);
        syncState();
        scoreboardManager.stopVote();
    }



    public void requestPrivateChat(ClocktowerPlayer player) {
        scoreboardManager.requestPrivateChat(player);
    }

    public void requestTeleportToPlayer(ClocktowerPlayer player) {
        scoreboardManager.requestTeleportToPlayer(player);
    }

    public void requestTeleportToHouse(ClocktowerPlayer player) {
        scoreboardManager.requestTeleportToHouse(player);
    }

    public void connectToTown(String townName) {
        townConnectionHandler.connect(townName);
    }

    public void disconnectFromTown() {
        townConnectionHandler.disconnect();
    }


    public void distributeRolesToTown() {
        sendToAllClients(new ShowRoleAnimationPacket());
    }



    public void onSSEEvent(ConnectionEvent event) {
        try {
            switch (event) {
                case StatusChanged(TownConnectionStatus status) -> {
                    if (status == TownConnectionStatus.DISCONNECTED) {
                        this.state = ClocktowerState.EMPTY;
                        syncState();
                        break;
                    }
                    this.updateConnectionStatus(status);

                }
                case DataReceived(String data, String townName) -> {
                    this.state = ClocktowerStateConverter.parseJsonToState(this.state, data, townName);
                    syncState();
                }
            }   
        } catch (IOException e) {
            Sttk.LOGGER.error("Failed to parse SSE data: {}", e.getMessage());
        }

    }








    private void syncState() {
        sendToAllClients(new SyncStatePayload(state));
        scoreboardManager.onStateChange(state);
    }

    public void broadcastStateToClient(ServerPlayer player){
        SyncStatePayload payload = new SyncStatePayload(state);
        ServerPlayNetworking.send(player, payload);
    }


    public void sendToAllClients(CustomPacketPayload payload) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()){
            ServerPlayNetworking.send(player, payload);
        }
    }




}

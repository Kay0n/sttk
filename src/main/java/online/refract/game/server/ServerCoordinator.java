package online.refract.game.server;

import online.refract.network.S2CPackets.AssetResponsePayload;
import online.refract.network.S2CPackets.ShowRoleAnimationPayload;
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
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;


public class ServerCoordinator {


    private ClocktowerState state = ClocktowerState.EMPTY;
    private MinecraftServer server;
    private ScoreboardManager scoreboardManager;
    private TownConnectionHandler townConnectionHandler;
    private ServerAssetCache assetCache;

    public ServerCoordinator(MinecraftServer server, ScoreboardManager scoreboardManager, TownConnectionHandler townConnectionHandler, ServerAssetCache assetCache) {
        this.server = server;
        this.scoreboardManager = scoreboardManager;
        this.townConnectionHandler = townConnectionHandler;
        this.townConnectionHandler.setConnectionListener(event -> server.executeBlocking(() -> this.onSSEEvent(event)));
        this.assetCache = assetCache;
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
        state = state.mapPlayers(p -> {
            if (Objects.equals(p.linkedMinecraftUsername(), linkedMinecraftUsername)) {
                return p.withLinkedMinecraftUsername(null);
            }
            if (Objects.equals(p.name(), clocktowerPlayerName)) {
                return p.withLinkedMinecraftUsername(linkedMinecraftUsername);
            }
            return p;
        });
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
        sendToAllClients(new ShowRoleAnimationPayload());
    }

    public void onPlayerJoin(ServerGamePacketListenerImpl handler){
        broadcastStateToClient(handler.getPlayer());
    }


    public void sendAssetsToClient(ServerPlayer player, List<String> assetUrls) {
        int chunkSize   = AssetResponsePayload.CHUNK_SIZE;
        for (String url : assetUrls) {
            byte[] asset = assetCache.getOrFetch(url).join();
            int totalChunks = (int) Math.ceil((double) asset.length / chunkSize);
            for (int i = 0; i < totalChunks; i++) {
                int start = i * chunkSize;
                int end = Math.min(start + chunkSize, asset.length);
                byte[] chunk = Arrays.copyOfRange(asset, start, end);
                ServerPlayNetworking.send(player, new AssetResponsePayload(url, i, totalChunks, chunk));
            }
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


    public void onSSEEvent(ConnectionEvent event) {
        try {
            switch (event) {
                case StatusChanged(TownConnectionStatus status) -> {
                    if (status != TownConnectionStatus.CONNECTED) {
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
        } 
        catch (IOException e) {
            Sttk.LOGGER.error("Failed to parse SSE data: {}", e.getMessage());
        }
    }




}

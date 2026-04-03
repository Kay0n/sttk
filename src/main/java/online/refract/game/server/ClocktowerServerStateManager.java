package online.refract.game.server;

import online.refract.network.S2CPackets.SyncStatePayload;
import online.refract.game.state.ClocktowerState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;


public class ClocktowerServerStateManager {


    private ClocktowerState state = ClocktowerState.EMPTY;
    private MinecraftServer server;
    private ScoreboardManager scoreboardManager;

    public ClocktowerServerStateManager(MinecraftServer server, ScoreboardManager scoreboardManager){
        this.server = server;
        this.scoreboardManager = scoreboardManager;
    }

    public void updateVoteActive(boolean active) {
        state = state.withVoteActive(active);
        syncState();
    }

    public void updatePlayerLink(String clocktowerPlayerName, String linkedMinecraftUsername) {
        state = state.withUpdatedPlayer(
            clocktowerPlayerName,
            player -> player.withLinkedMinecraftUsername(linkedMinecraftUsername)
        );
        syncState();
    }

    public void setEmptyState(){
        state = ClocktowerState.EMPTY;
        syncState();
    }


    public void updateState(ClocktowerState newState) {
        state = newState;
        syncState();
    }

    private void syncState() {
        SyncStatePayload payload = new SyncStatePayload(state);
        for (ServerPlayer player : server.getPlayerList().getPlayers()){
            ServerPlayNetworking.send(player, payload);
        }
        scoreboardManager.onStateChange(state);
    }

    public void broadcastStateToClient(ServerPlayer player){
        SyncStatePayload payload = new SyncStatePayload(state);
        ServerPlayNetworking.send(player, payload);
    }

    public ClocktowerState getState() {
        return state;
    }
}

package online.refract.game.server;

import online.refract.network.C2SPackets;
import online.refract.network.S2CPackets.SyncStatePayload;
import online.refract.Sttk;
import online.refract.game.state.ClocktowerState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClocktowerServerStateManager {

    private final Logger LOGGER = LoggerFactory.getLogger(Sttk.MOD_ID);

    private ClocktowerState state = ClocktowerState.EMPTY;
    private MinecraftServer server;

    public ClocktowerServerStateManager(MinecraftServer server){
        this.server = server;
    }

    public void updateVoteActive(boolean active) {
        state = state.withVoteActive(active);
        broadcastStateToAllClients();
        LOGGER.debug("Vote state updated to: {}", active);
    }

    public void handlePacketUpdate(String clocktowerPlayerName, String linkedMinecraftUsername) {
        state = state.withUpdatedPlayer(
            clocktowerPlayerName,
            player -> player.withLinkedMinecraftUsername(linkedMinecraftUsername)
        );
        broadcastStateToAllClients();
        LOGGER.debug("Linked username for player {}: {}", clocktowerPlayerName, linkedMinecraftUsername);
    }

    public void setEmptyState(){
        state = ClocktowerState.EMPTY;
        broadcastStateToAllClients();
        LOGGER.info("SSE state is empty");
    }


    public void updateState(ClocktowerState newState) {
        state = newState;
        broadcastStateToAllClients();
        LOGGER.info("SSE state updated: Town={}, Day={}, Phase={}", 
            newState.townName(), newState.currentDay(), newState.currentPhase());
    }

    private void broadcastStateToAllClients() {
        SyncStatePayload payload = new SyncStatePayload(state);
        for (ServerPlayer player : server.getPlayerList().getPlayers()){
            ServerPlayNetworking.send(player, payload);
        }
    }

    public void broadcastStateToClient(ServerPlayer player){
        SyncStatePayload payload = new SyncStatePayload(state);
        ServerPlayNetworking.send(player, payload);
    }

    public ClocktowerState getState() {
        return state;
    }
}

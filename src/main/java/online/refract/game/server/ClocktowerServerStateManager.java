package online.refract.game.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.server.level.ServerPlayer;
import online.refract.Sttk;
import online.refract.game.state.ClocktowerPlayer;
import online.refract.game.state.ClocktowerState;
import online.refract.game.state.Enums.TownConnectionStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ClocktowerServerStateManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(Sttk.MOD_ID);

    private static ClocktowerState currentState = ClocktowerState.EMPTY;
    private static TownConnectionStatus connectionStatus = TownConnectionStatus.DISCONNECTED;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();





    public static void setVotingActive(boolean active) {
        currentState = currentState.withVoteActive(active);
        syncToAllPlayers();
        LOGGER.debug("Vote state updated to: {}", active);
    }

    public static void linkUsername(ClocktowerPlayer playerId, String minecraftUsername) {
        for (ClocktowerPlayer player : currentState.players()) {
            if (player.uuid().toString().equals(playerId)) {
                currentState = currentState.withUpdatedPlayer(player.uuid(), p ->
                    p.withLinkedMinecraftUsername(minecraftUsername)
                );
                syncToAllPlayers();
                LOGGER.debug("Linked username for player {}: {}", playerId, minecraftUsername);
                return;
            }
        }
    }

    // Called when player joins
    public static void syncToPlayer(ServerPlayer player) {
        // Send current state via packet
        if (currentState != ClocktowerState.EMPTY) {
            LOGGER.debug("Syncing state to player: {}", player.getScoreboardName());
        }
    }

    // Called when player leaves
    public static void onPlayerLeave(ServerPlayer player) {
        LOGGER.debug("Player left: {}", player.getScoreboardName());
    }

    // Sync state to all players
    private static void syncToAllPlayers() {
        currentState = currentState.withTownConnectionStatus(connectionStatus);
    }

}
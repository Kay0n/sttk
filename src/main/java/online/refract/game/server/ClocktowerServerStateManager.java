package online.refract.game.server;

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



    public static void setVotingActive(boolean active) {
        currentState = currentState.withVoteActive(active);
        syncToAllPlayers();
        LOGGER.debug("Vote state updated to: {}", active);
    }

    // TODO: remove link from other players
    public static void linkUsername(ClocktowerPlayer linkedPlayer, String minecraftUsername) {
        for (ClocktowerPlayer player : currentState.players()) {
            if (!player.name().equals(linkedPlayer.name())) {
                continue;
            }
            currentState = currentState.withUpdatedPlayer(linkedPlayer.name(), p ->
                p.withLinkedMinecraftUsername(minecraftUsername)
            );
            syncToAllPlayers();
            LOGGER.debug("Linked username for player {}: {}", linkedPlayer.name(), minecraftUsername);
            return;
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
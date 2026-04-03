package online.refract.game.server;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import online.refract.game.state.ClocktowerPlayer;
import online.refract.game.state.ClocktowerState;
import online.refract.game.state.Enums.GamePhase;

public class ScoreboardManager {

    private static final String GLOBAL_HOLDER = "#botc";

    private static final String IS_NIGHT = "is_night";
    private static final String DAY_NUM = "day_num";
    private static final String PLAYER_COUNT = "player_count";
    private static final String IS_VOTE_ACTIVE = "is_vote_active";
    private static final String VOTE_STARTING_PLAYER = "vote_starting_player";

    private static final String PLAYER_ID = "player_id";
    private static final String IS_DEAD = "is_dead";
    private static final String HAS_USED_GHOST_VOTE = "has_used_ghost_vote";
    private static final String ST_PRIVATE_CHAT = "st_private_chat";
    private static final String ST_TP_PLAYER = "st_tp_player";
    private static final String ST_TP_HOUSE = "st_tp_house";

    private final MinecraftServer server;

    public ScoreboardManager(MinecraftServer server) {
        this.server = server;
        registerObjectives();
    }



    public void onStateChange(ClocktowerState state) {
        Scoreboard scoreboard = server.getScoreboard();

        setScore(scoreboard, GLOBAL_HOLDER, IS_NIGHT, state.currentPhase() == GamePhase.NIGHT ? 1 : 0);
        setScore(scoreboard, GLOBAL_HOLDER, DAY_NUM, state.currentDay());
        setScore(scoreboard, GLOBAL_HOLDER, PLAYER_COUNT, state.players().size());
        setScore(scoreboard, GLOBAL_HOLDER, IS_VOTE_ACTIVE, state.isVoteActive() ? 1 : 0);

        List<ClocktowerPlayer> players = state.players();
        for (int i = 0; i < players.size(); i++) {
            ClocktowerPlayer player = players.get(i);
            String minecraftUsername = player.linkedMinecraftUsername();
            int playerId = i + 1;

            setScore(scoreboard, minecraftUsername, PLAYER_ID, playerId);
            setScore(scoreboard, minecraftUsername, IS_DEAD, player.isDead() ? 1 : 0);
            setScore(scoreboard, minecraftUsername, HAS_USED_GHOST_VOTE, player.hasUsedGhostVote() ? 1 : 0);
        }
    }

    public void startVoteForPlayer(ClocktowerPlayer player, ClocktowerState state) {
        Scoreboard scoreboard = server.getScoreboard();

        int startingId = getPlayerIdFromState(player, state);
        if (startingId == -1) return;

        setScore(scoreboard, GLOBAL_HOLDER, VOTE_STARTING_PLAYER, startingId);
        setScore(scoreboard, GLOBAL_HOLDER, IS_VOTE_ACTIVE, 1);
    }

    public void stopVote(){
        Scoreboard scoreboard = server.getScoreboard();

        setScore(scoreboard, GLOBAL_HOLDER, VOTE_STARTING_PLAYER, 0);
        setScore(scoreboard, GLOBAL_HOLDER, IS_VOTE_ACTIVE, 0);
    }

    public void requestPrivateChat(ClocktowerPlayer player) {
        setPlayerFlag(player.linkedMinecraftUsername(), ST_PRIVATE_CHAT);
    }

    public void requestTeleportToPlayer(ClocktowerPlayer player) {
        setPlayerFlag(player.linkedMinecraftUsername(), ST_TP_PLAYER);
    }

    public void requestTeleportToHouse(ClocktowerPlayer player) {
        setPlayerFlag(player.linkedMinecraftUsername(), ST_TP_HOUSE);
    }


    private void setPlayerFlag(String minecraftUsername, String objective) {
        if(minecraftUsername == "") { return; }
        setScore(server.getScoreboard(), minecraftUsername, objective, 1);
    }

    private void setScore(Scoreboard scoreboard, String holder, String objectiveName, int value) {
        Objective objective = scoreboard.getObjective(objectiveName);
        if (objective == null) return;
        scoreboard.getOrCreatePlayerScore(ScoreHolder.forNameOnly(objectiveName), objective).set(value);
    }

    private int getPlayerIdFromState(ClocktowerPlayer player, ClocktowerState state) {
        List<ClocktowerPlayer> players = state.players();
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).linkedMinecraftUsername().equals(player.linkedMinecraftUsername())) {
                return i + 1;
            }
        }
        return -1;
    }

    private void registerObjectives() {
        ServerScoreboard scoreboard = server.getScoreboard();
        ObjectiveCriteria dummy = ObjectiveCriteria.DUMMY;

        for (String name : List.of(
                IS_NIGHT, DAY_NUM, PLAYER_COUNT, IS_VOTE_ACTIVE, VOTE_STARTING_PLAYER,
                PLAYER_ID, IS_DEAD, HAS_USED_GHOST_VOTE,
                ST_PRIVATE_CHAT, ST_TP_PLAYER, ST_TP_HOUSE
        )) {
            if (scoreboard.getObjective(name) == null) {
                scoreboard.addObjective(name, dummy, Component.literal(name), ObjectiveCriteria.RenderType.INTEGER, true, null);
            }
        }
    }
}

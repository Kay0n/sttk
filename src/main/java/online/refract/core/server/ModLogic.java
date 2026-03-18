package online.refract;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;



public class ModLogic {

    public static void handleAction(ServerPlayer st, ServerPlayer target, String action) {
        switch (action) {
            case "TOGGLE_DEAD" -> toggleScore(target, "sttk_dead");
            case "TOGGLE_NOMINATE" -> toggleScore(target, "sttk_nominated");
            case "TOGGLE_BLOCK" -> toggleScore(target, "sttk_block");
            case "TOGGLE_GHOSTVOTE" -> toggleScore(target, "sttk_ghostvote");
            case "TELEPORT" -> {
                int houseId = getScore(target, "sttk_house_id");
                setGlobalScore(st.getServer(), "sttk_tp_target", houseId);
                // st.teleport(target.getWorld(), target.getX(), target.getY(), target.getZ(), 0, 0);
            }
        }
    }


    public static void resetGame(MinecraftServer server) {
        setGlobalScore(server, "sttk_cycle", 1);
    }


    private static void toggleScore(ServerPlayer player, String objName) {
        int current = getScore(player, objName);
        setScore(player, objName, current == 1 ? 0 : 1);
    }


    public static int getScore(ServerPlayer player, String objName) {
        Scoreboard sb = player.getScoreboard();
        Objective obj = sb.getObjective(objName);
        if (obj == null) return 0;
        return sb.getOrCreatePlayerScore(player, obj).get();
    }


    public static void setScore(ServerPlayer player, String objName, int value) {
        Scoreboard sb = player.getScoreboard();
        Objective obj = sb.getObjective(objName);
        if (obj == null) {
            obj = sb.addObjective(objName, ObjectiveCriteria.DUMMY, Component.nullToEmpty(objName), ObjectiveCriteria.RenderType.INTEGER, true, null);
        }
        sb.getOrCreatePlayerScore(player, obj).set(value);
    }


    public static void setGlobalScore(MinecraftServer server, String objName, int value) {
        Scoreboard sb = server.getScoreboard();
        Objective obj = sb.getObjective(objName);
        if (obj == null) {
            obj = sb.addObjective(objName, ObjectiveCriteria.DUMMY, Component.nullToEmpty(objName), ObjectiveCriteria.RenderType.INTEGER, true, null);
        }
        ScoreHolder globalHolder = ScoreHolder.forNameOnly("#GLOBAL");
        sb.getOrCreatePlayerScore(globalHolder, obj).set(value);
    }
}
package online.refract;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;



public class ModLogic {

    public static void handleAction(ServerPlayerEntity st, ServerPlayerEntity target, String action) {
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


    private static void toggleScore(ServerPlayerEntity player, String objName) {
        int current = getScore(player, objName);
        setScore(player, objName, current == 1 ? 0 : 1);
    }


    public static int getScore(ServerPlayerEntity player, String objName) {
        Scoreboard sb = player.getScoreboard();
        ScoreboardObjective obj = sb.getNullableObjective(objName);
        if (obj == null) return 0;
        return sb.getOrCreateScore(player, obj).getScore();
    }


    public static void setScore(ServerPlayerEntity player, String objName, int value) {
        Scoreboard sb = player.getScoreboard();
        ScoreboardObjective obj = sb.getNullableObjective(objName);
        if (obj == null) {
            obj = sb.addObjective(objName, ScoreboardCriterion.DUMMY, Text.of(objName), ScoreboardCriterion.RenderType.INTEGER, true, null);
        }
        sb.getOrCreateScore(player, obj).setScore(value);
    }


    public static void setGlobalScore(MinecraftServer server, String objName, int value) {
        Scoreboard sb = server.getScoreboard();
        ScoreboardObjective obj = sb.getNullableObjective(objName);
        if (obj == null) {
            obj = sb.addObjective(objName, ScoreboardCriterion.DUMMY, Text.of(objName), ScoreboardCriterion.RenderType.INTEGER, true, null);
        }
        ScoreHolder globalHolder = ScoreHolder.fromName("#GLOBAL");
        sb.getOrCreateScore(globalHolder, obj).setScore(value);
    }
}
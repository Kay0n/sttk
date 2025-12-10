package online.refract.client;

import net.minecraft.client.Minecraft;

public class ClientActionHandler {



    public void startVote() {
        debug("Vote");
    }

    public void setNight() {
        debug("Night");
    }

    public void setEvening() {
        debug("Evening");
    }

    public void setDay() {
        debug("Day");
    }

    public void resetScores() {
        debug("Scores");
    }

    public void reorderPlayers() {
        debug("Reorder") ;
    }

    public void homeTeleport(String playerName) {
        debug("Home Teleport: " + playerName);
    }

    public void kill(String playerName) {
        debug("Kill: " + playerName);
    }

    public void nominate(String playerName) {
        debug("Nominate: " + playerName);
    } 

    public void putOnBlock(String playerName) {
        debug("Put on Block: " + playerName);
    }

    public void ghostVote(String playerName) {
        debug("Ghost Vote: " + playerName);
    }

    public void setTimer(int seconds) {
        debug("Timer: " + seconds);
    }


    public void debug(String msg) {
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.displayClientMessage(
                net.minecraft.network.chat.Component.nullToEmpty("§b[STTK]: §f" + msg), false);
        }
    }

    


    
}

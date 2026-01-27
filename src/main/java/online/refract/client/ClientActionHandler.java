package online.refract.client;

import java.util.ArrayList;
import java.util.UUID;

import online.refract.network.NetworkEnums.GameActionType;
import online.refract.network.ServerBoundPackets.GameActionPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ClientActionHandler {


    public void send(CustomPacketPayload payload){
    }
 
    // represent "game" variables
    public void startVote() {
        ClientPlayNetworking.send(
            new GameActionPayload(GameActionType.START_VOTE)
        );
        debug("Vote");
    }

    public void setNight() {
        ClientPlayNetworking.send(
            new GameActionPayload(GameActionType.SET_NIGHT)
        );
        debug("Night");
    }
    public void setEvening() {
        ClientPlayNetworking.send(
            new GameActionPayload(GameActionType.SET_EVENING)
        );
        debug("Evening");
    }
    public void setDay() {
        ClientPlayNetworking.send(
            new GameActionPayload(GameActionType.SET_DAY)
        );
        debug("Day");
    }
    
    public void resetScores() { 
        ClientPlayNetworking.send(
            new GameActionPayload(GameActionType.RESET_SCORES)
        );
        debug("Scores");
    }


    public void reorderPlayers(ArrayList<UUID> newOrder) {  

        debug("Reorder") ;
    }


    public void homeTeleport(UUID playerUUID) {
        debug("Home Teleport: " + playerUUID.toString());
    }
    public void kill(UUID playerUUID) {
        debug("Kill: " + playerUUID.toString());
    }
    public void nominate(UUID playerUUID) {
        debug("Nominate: " + playerUUID.toString());
    } 
    public void putOnBlock(UUID playerUUID) {
        debug("Put on Block: " + playerUUID.toString());
    }
    public void ghostVote(UUID playerUUID) {
        debug("Ghost Vote: " + playerUUID.toString());
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

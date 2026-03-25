package online.refract.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import online.refract.game.state.ClocktowerPlayer;
import online.refract.network.C2SPackets;
import net.minecraft.client.Minecraft;

public class ClientActionHandler {

    
    public void sendToggleVote(boolean active) {
        ClientPlayNetworking.send(new C2SPackets.ToggleVotePayload(active));
    }
    
    public void sendLinkUsername(ClocktowerPlayer player, String minecraftUsername) {
        String playerId = player.uuid.toString();
        ClientPlayNetworking.send(new C2SPackets.LinkUsernamePayload(playerId, minecraftUsername));
    }
    
    
    public void debug(String msg) {
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.displayClientMessage(
                net.minecraft.network.chat.Component.nullToEmpty("§b[STTK]: §f" + msg), false);
        }
    }

}
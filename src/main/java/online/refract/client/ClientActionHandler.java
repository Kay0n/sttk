package online.refract.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import online.refract.game.state.ClocktowerPlayer;
import online.refract.network.C2SPackets;
import net.minecraft.client.Minecraft;

public class ClientActionHandler {

    
    public void sendStartVoteForPlayer(ClocktowerPlayer player) {
        ClientPlayNetworking.send(new C2SPackets.StartVoteForPlayerPayload(player));
    }

    public void sendStopVote() {
        ClientPlayNetworking.send(new C2SPackets.StopVotePayload());
    }

    public void sendDistributeRolesToTown() {
        ClientPlayNetworking.send(new C2SPackets.DistributeRolesToTownPayload());
    }

    public void sendRequestPrivateChat(ClocktowerPlayer player) {
        ClientPlayNetworking.send(new C2SPackets.RequestPrivateChatPayload(player));
    }

    public void sendRequestTeleportToPlayer(ClocktowerPlayer player) {
        ClientPlayNetworking.send(new C2SPackets.RequestTeleportToPlayerPayload(player));
    }

    public void sendRequestTeleportToHouse(ClocktowerPlayer player) {
        ClientPlayNetworking.send(new C2SPackets.RequestTeleportToHousePayload(player));
    }
    
   public void sendLinkUsername(ClocktowerPlayer player, String minecraftUsername) {
        ClientPlayNetworking.send(new C2SPackets.LinkUsernamePayload(player, minecraftUsername));
    }
    
    public void sendConnectToTown(String townName) {
        ClientPlayNetworking.send(new C2SPackets.ConnectToTownPayload(townName));
    }

    public void sendDisconnectFromTown() {
        ClientPlayNetworking.send(new C2SPackets.DisconnectFromTownPayload());
    }
    
    public static void debug(String msg) {
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.displayClientMessage(
                net.minecraft.network.chat.Component.nullToEmpty("§b[STTK]: §f" + msg), false);
        }
    }

}
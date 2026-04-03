package online.refract.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import online.refract.Sttk;
import online.refract.client.render.hud.RoleRevealAnimation;
import online.refract.game.state.ClocktowerPlayer;
import online.refract.game.state.ClocktowerState;
import online.refract.network.C2SPackets;
import net.minecraft.client.Minecraft;

public class ClientCoordinator {

    private final ClocktowerClientState clientState;

    public ClientCoordinator(ClocktowerClientState clientState) {
        this.clientState = clientState;
    }

    public ClocktowerState getState() {
        return clientState.getState();
    }

    public void onRecieveState(ClocktowerState newState) {
        clientState.onRecieveState(newState);
    }

    
    public void playShowRoleAnimation() {
        for (ClocktowerPlayer player : getState().players()) {
            String localPlayerName = Minecraft.getInstance().player.getName().getString();
            if (player.linkedMinecraftUsername() == localPlayerName) {

                RoleRevealAnimation.play(player.roleName());
            }
        }
    }


    // send packets to server
    public void startVoteForPlayer(ClocktowerPlayer player) {
        ClientPlayNetworking.send(new C2SPackets.StartVoteForPlayerPayload(player));
    }

    public void stopVote() {
        ClientPlayNetworking.send(new C2SPackets.StopVotePayload());
    }

    public void distributeRolesToTown() {
        ClientPlayNetworking.send(new C2SPackets.DistributeRolesToTownPayload());
    }

    public void requestPrivateChat(ClocktowerPlayer player) {
        ClientPlayNetworking.send(new C2SPackets.RequestPrivateChatPayload(player));
    }

    public void requestTeleportToPlayer(ClocktowerPlayer player) {
        ClientPlayNetworking.send(new C2SPackets.RequestTeleportToPlayerPayload(player));
    }

    public void requestTeleportToHouse(ClocktowerPlayer player) {
        ClientPlayNetworking.send(new C2SPackets.RequestTeleportToHousePayload(player));
    }
    
   public void linkUsername(ClocktowerPlayer player, String minecraftUsername) {
        ClientPlayNetworking.send(new C2SPackets.LinkUsernamePayload(player, minecraftUsername));
    }
    
    public void connectToTown(String townName) {
        ClientPlayNetworking.send(new C2SPackets.ConnectToTownPayload(townName));
    }

    public void disconnectFromTown() {
        ClientPlayNetworking.send(new C2SPackets.DisconnectFromTownPayload());
    }



    // util
    public static void debug(String msg) {
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.displayClientMessage(
                net.minecraft.network.chat.Component.nullToEmpty("§b[STTK]: §f" + msg), false);
        }
    }

}
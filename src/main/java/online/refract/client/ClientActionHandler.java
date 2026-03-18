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
 




    public void homeTeleport(UUID playerUUID) {
        debug("Home Teleport: " + playerUUID.toString());
    }



    public void debug(String msg) {
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.displayClientMessage(
                net.minecraft.network.chat.Component.nullToEmpty("§b[STTK]: §f" + msg), false);
        }
    }


    


    
}

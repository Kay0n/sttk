package online.refract;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import online.refract.game.server.ClocktowerServerStateManager;
import online.refract.game.server.ModCommands;
import online.refract.network.S2CPackets;
import online.refract.network.C2SPackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sttk implements ModInitializer {
    public static final String MOD_ID = "sttk";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static int SERVER_PLAYER_COUNT = 13;

    
    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }


    @Override
    public void onInitialize() {
        

        PayloadTypeRegistry.playC2S().register(C2SPackets.ToggleVotePayload.ID, C2SPackets.ToggleVotePayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(C2SPackets.LinkUsernamePayload.ID, C2SPackets.LinkUsernamePayload.STREAM_CODEC);

        PayloadTypeRegistry.playS2C().register(S2CPackets.SyncStateS2CPayload.ID, S2CPackets.SyncStateS2CPayload.CODEC);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ModCommands.register(dispatcher);
        });


        ServerPlayNetworking.registerGlobalReceiver(C2SPackets.ToggleVotePayload.ID, (payload, context) -> {
            ClocktowerServerStateManager.setVotingActive(payload.active());
        });

        ServerPlayNetworking.registerGlobalReceiver(C2SPackets.LinkUsernamePayload.ID, (payload, context) -> {
            ClocktowerServerStateManager.linkUsername(payload.playerId(), payload.minecraftUsername());
        });
    }
}
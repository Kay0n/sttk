package online.refract;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
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
        
        PayloadTypeRegistry.playC2S().register(SttkPayloads.ActionPayload.ID, SttkPayloads.ActionPayload.CODEC);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ModCommands.register(dispatcher);
        });

        ServerPlayNetworking.registerGlobalReceiver(SttkPayloads.ActionPayload.ID, (payload, context) -> {
            ServerPlayer player = context.player();
            
            if (!player.hasPermissions(2)) return;

            context.server().execute(() -> {
                ServerPlayer target = (ServerPlayer) player.level().getEntity(payload.targetId());
                if (target != null) {
                    ModLogic.handleAction(player, target, payload.action());
                } else if (payload.action().equals("RESET")) {
                    ModLogic.resetGame(context.server());
                }
            });
        });
    }
}
package online.refract;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sttk implements ModInitializer {
    public static final String MOD_ID = "sttk";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static int SERVER_PLAYER_COUNT = 13;

    
    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }


    @Override
    public void onInitialize() {
        
        PayloadTypeRegistry.playC2S().register(SttkPayloads.ActionPayload.ID, SttkPayloads.ActionPayload.CODEC);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ModCommands.register(dispatcher);
        });

        ServerPlayNetworking.registerGlobalReceiver(SttkPayloads.ActionPayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            
            if (!player.hasPermissionLevel(2)) return;

            context.server().execute(() -> {
                ServerPlayerEntity target = (ServerPlayerEntity) player.getWorld().getEntityById(payload.targetId());
                if (target != null) {
                    ModLogic.handleAction(player, target, payload.action());
                } else if (payload.action().equals("RESET")) {
                    ModLogic.resetGame(context.server());
                }
            });
        });
    }
}
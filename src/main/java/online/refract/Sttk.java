package online.refract;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.resources.ResourceLocation;
import online.refract.game.server.ModCommands;
import online.refract.game.server.ScoreboardManager;
import online.refract.game.server.ServerAssetCache;
import online.refract.game.server.ServerPacketReceiver;
import online.refract.http.TownConnectionHandler;
import online.refract.game.server.ServerCoordinator;
import online.refract.network.S2CPackets;
import online.refract.network.C2SPackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sttk implements ModInitializer {
    public static final String     MOD_ID = "sttk";
    public static final Logger     LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static int              SERVER_PLAYER_COUNT = 13;
    public static final String     SSE_BASE_URL = "http://127.0.0.1:3000/stream"; 

    public ServerCoordinator serverCoordinator;
    public TownConnectionHandler townConnectionHandler;
    public ScoreboardManager scoreboardManager;
    public ServerAssetCache assetCache;




    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }


    @Override
    public void onInitialize() {

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            scoreboardManager = new ScoreboardManager(server);
            townConnectionHandler = new TownConnectionHandler(SSE_BASE_URL);
            assetCache = new ServerAssetCache();
            serverCoordinator = new ServerCoordinator(server, scoreboardManager, townConnectionHandler, assetCache);
            ServerPacketReceiver.setCoordinator(serverCoordinator);
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            townConnectionHandler.disconnect();
        });
        
        C2SPackets.registerPackets();
        S2CPackets.registerPackets();

        ServerPacketReceiver.register();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ModCommands.register(dispatcher);
        });

    }

    
}

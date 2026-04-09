package online.refract.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.KeyMapping;
import online.refract.client.gui.screens.grimiore.GrimoireScreen;
import online.refract.client.gui.screens.script.ScriptScreen;
import online.refract.client.render.hud.RoleRevealAnimation;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SttkClient implements ClientModInitializer {
    
    public static final String MOD_ID = "sttk";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static KeyMapping OPEN_GRIMOIRE_KEY;
    public static KeyMapping OPEN_SCRIPT_KEY;

    public final ClientAssetCache assetCache = new ClientAssetCache();
    public final ClientCoordinator coordinator = new ClientCoordinator(assetCache);

    private static boolean wasGrimoireKeyDown = false;
    private static boolean wasScriptKeyDown = false;


    
    @Override
    public void onInitializeClient() {
        OPEN_GRIMOIRE_KEY = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.sttk.grimoire",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "category.sttk"
        ));
        OPEN_SCRIPT_KEY = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.sttk.script",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            "category.sttk"
        ));

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            coordinator.clearClient();
        });


        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            if (!client.player.hasPermissions(2)) return;

            boolean isGrimoireDown = OPEN_GRIMOIRE_KEY.isDown();
            if (isGrimoireDown && !wasGrimoireKeyDown) {
                if (client.screen instanceof GrimoireScreen) {
                    client.setScreen(null);
                    return;
                } 
                GrimoireScreen grimoireScreen = new GrimoireScreen(coordinator);
                client.setScreen(grimoireScreen);
                coordinator.setGrimoireScreen(grimoireScreen); 
            }
            wasGrimoireKeyDown = isGrimoireDown;

            boolean isScriptDown = OPEN_SCRIPT_KEY.isDown();
            if (isScriptDown && !wasScriptKeyDown) {
                if (client.screen instanceof ScriptScreen) {
                    client.setScreen(null);
                    return;
                } 
                ScriptScreen scriptScreen = new ScriptScreen(coordinator);
                client.setScreen(scriptScreen); 
            }
            wasScriptKeyDown = isScriptDown;

        });


        RoleRevealAnimation.register(assetCache);

        ClientPacketReceiver.register();
        ClientPacketReceiver.setCoordinator(coordinator);
        

    }
}
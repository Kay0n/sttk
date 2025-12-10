package online.refract.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import online.refract.client.gui.GrimoireScreen;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class SttkClient implements ClientModInitializer {
    
    public static final String MOD_ID = "sttk";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static KeyMapping OPEN_GRIMOIRE_KEY;

    
    @Override
    public void onInitializeClient() {
        OPEN_GRIMOIRE_KEY = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.sttk.grimoire",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "category.sttk"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_GRIMOIRE_KEY.consumeClick()) {
                if (client.player == null) return;
                if (!client.player.hasPermissions(2)) return;

                if (client.screen instanceof GrimoireScreen) {
                    client.setScreen(null);
                } else {
                    client.setScreen(new GrimoireScreen());
                }
            }
        });
    }
}
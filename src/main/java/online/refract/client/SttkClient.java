package online.refract.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import online.refract.client.gui.GrimoireScreen;

import org.lwjgl.glfw.GLFW;



public class SttkClient implements ClientModInitializer {
    
    public static KeyBinding OPEN_GRIMOIRE_KEY;

    
    @Override
    public void onInitializeClient() {
        OPEN_GRIMOIRE_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.sttk.grimoire",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "category.sttk"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_GRIMOIRE_KEY.wasPressed()) {
                if (client.player == null) return;
                if (!client.player.hasPermissionLevel(2)) return;

                if (client.currentScreen instanceof GrimoireScreen) {
                    client.setScreen(null);
                } else {
                    client.setScreen(new GrimoireScreen());
                }
            }
        });
    }
}
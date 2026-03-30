package online.refract.client;

import online.refract.client.gui.grimiore.GrimoireScreen;
import online.refract.game.state.ClocktowerState;
import online.refract.game.state.Enums.TownConnectionStatus;

public class ClocktowerClientState {
    
    private static ClocktowerState state = ClocktowerState.EMPTY;
    private static TownConnectionStatus previousConnectionStatus = TownConnectionStatus.DISCONNECTED;
    private static GrimoireScreen grimoireScreen;
    
    public static void setGrimoireScreen(GrimoireScreen screen) {
        grimoireScreen = screen;
    }
    
    public static void onRecieveState(ClocktowerState newState) {
        state = newState;
        
        TownConnectionStatus newStatus = newState.townConnectionStatus();
        // Notify UI only if connection status changed
        if (newStatus != previousConnectionStatus && grimoireScreen != null) {
            previousConnectionStatus = newStatus;
            grimoireScreen.onStateUpdated();
        }
        
 
    }
    
    public static ClocktowerState getState() {
        return state;
    }
}
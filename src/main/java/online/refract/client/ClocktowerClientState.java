package online.refract.client;

import online.refract.client.gui.screens.grimiore.GrimoireScreen;
import online.refract.game.state.ClocktowerState;
// import online.refract.game.state.Enums.TownConnectionStatus;

public class ClocktowerClientState {
    
    private static ClocktowerState state = ClocktowerState.EMPTY;
    // private static TownConnectionStatus previousConnectionStatus = TownConnectionStatus.DISCONNECTED;
    private static GrimoireScreen grimoireScreen;
    
    public static void setGrimoireScreen(GrimoireScreen screen) {
        grimoireScreen = screen;
    }
    
    public static void onRecieveState(ClocktowerState newState) {
        state = newState;
        
        // // notify UI only if connection status changed
        // TownConnectionStatus newStatus = newState.townConnectionStatus();
        // if (newStatus != previousConnectionStatus && grimoireScreen != null) {
        //     previousConnectionStatus = newStatus;
        //     grimoireScreen.onStateUpdated();
        // }

        grimoireScreen.onStateUpdated();
    }
    
    public static ClocktowerState getState() {
        return state;
    }
}
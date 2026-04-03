package online.refract.client.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import online.refract.client.ClientActionHandler;
import online.refract.client.ClocktowerClientState;
import online.refract.game.state.ClocktowerState;
import online.refract.game.state.Enums.TownConnectionStatus;

public class TownModal extends Modal {

    private EditBox townNameBox;

    public TownModal(ClientActionHandler actionHandler) {
        super(actionHandler, "Connect to Town", 200);
    }

    @Override
    public void init(int screenWidth, int screenHeight, Font font) {
        super.init(screenWidth, screenHeight, font);

        updateComponents();
    }




public void updateComponents() {
    String savedValue = this.townNameBox != null ? this.townNameBox.getValue() : "";
    boolean isFirstTimeOpen = this.townNameBox == null;

    ClocktowerState state = ClocktowerClientState.getState();
    TownConnectionStatus status = state.townConnectionStatus();

    this.clearWidgets();

    switch (status) {
        case INVALID_TOWN  -> this.title = "Invalid Town";
        case CONNECTION_LOST -> this.title = "Connection Lost";
        case CONNECTING    -> this.title = "Connecting...";
        case CONNECTED     -> this.title = "Connected to " + state.townName();
        default            -> this.title = "Connect to Town";
    }

    this.townNameBox = createEditBox("Town Name", 32);

    if (status == TownConnectionStatus.CONNECTED) {
        townNameBox.setValue(state.townName());
    } 
    else if (savedValue != "") {
        townNameBox.setValue(savedValue);
    }

    addEditBoxRow(this.townNameBox);

    Button connectButton = createButton(
        Component.literal(status == TownConnectionStatus.CONNECTED ? "Disconnect" : "Connect"),
        () -> {
            if (status == TownConnectionStatus.CONNECTED) {
                ClientActionHandler.debug("Disconnecting from town");
                actionHandler.sendDisconnectFromTown();
            } else {
                String townName = townNameBox.getValue();
                if (townName.isEmpty()) { return; }
                ClientActionHandler.debug("Attempting to connect to town: " + townName);
                title = "Connecting...";
                actionHandler.sendConnectToTown(townName);
            }
        }
    );

    addButtonRow(
        connectButton,
        createButton(Component.literal("Close"), this::closeModal)
    );

    rebuildLayout();
    
    if (!isFirstTimeOpen) {
        this.setFocus(this.townNameBox);
    }
}


}

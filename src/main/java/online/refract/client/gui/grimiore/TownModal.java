package online.refract.client.gui.grimiore;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import online.refract.client.ClientActionHandler;
import online.refract.client.ClocktowerClientState;
import online.refract.game.state.ClocktowerState;
import online.refract.game.state.Enums.TownConnectionStatus;
import online.refract.client.gui.components.Modal;

public class TownModal extends Modal {

    private EditBox townNameBox;

    public TownModal(ClientActionHandler actionHandler) {
        super(actionHandler, "Connect to Town", 200);
    }

    @Override
    public void init(int screenWidth, int screenHeight, Font font) {
        super.init(screenWidth, screenHeight, font);

        this.townNameBox = createEditBox("Town Name", 32);

        addEditBoxRow(this.townNameBox);
        
        ClocktowerState state = ClocktowerClientState.getState();
        TownConnectionStatus status = state.townConnectionStatus;
        
        // Update modal title with status message
        Component titleText = Component.nullToEmpty("Connect to Town");
        if (status == TownConnectionStatus.INVALID_TOWN) {
            titleText = Component.literal("Invalid Town");
        } else if (status == TownConnectionStatus.CONNECTION_LOST) {
            titleText = Component.literal("Connection Lost");
        } else if (status == TownConnectionStatus.CONNECTING) {
            titleText = Component.literal("Connecting...");
        }
        this.title = titleText.getString();

        addButton(Component.literal("Connect"), () -> {
            String townName = townNameBox.getValue();
            if (townName.isEmpty()) {
                actionHandler.debug("Town name is empty");
                closeModal();
                return;
            }
            actionHandler.debug("Attempting to connect to town: " + townName);
            this.title = "Connecting...";
            closeModal();
        });
    }
}

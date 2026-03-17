package online.refract.client.gui.modals;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import online.refract.client.ClientActionHandler;
import online.refract.client.gui.objects.Modal;

public class TownModal extends Modal {

    private EditBox townNameBox;
    private EditBox passwordBox;

    public TownModal(ClientActionHandler actionHandler) {
        super(actionHandler, "Connect to Town", 200);
    }

    @Override
    public void init(int screenWidth, int screenHeight, Font font) {
        super.init(screenWidth, screenHeight, font);

        this.townNameBox = createEditBox("Town Name", 32);
        this.passwordBox = createEditBox("Password", 32);

        addEditBoxRow(this.townNameBox);
        addEditBoxRow(this.passwordBox);
        addSpacerRow();
        
        addButton(Component.literal("Connect"), () -> {
            actionHandler.debug("Attempting to connect to town: " + townNameBox.getValue() + " with password: " + passwordBox.getValue());
            closeModal();
        });
    }
}

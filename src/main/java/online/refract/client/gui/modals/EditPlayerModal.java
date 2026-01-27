package online.refract.client.gui.modals;

import online.refract.client.ClientActionHandler;
import online.refract.client.gui.Modal;

public class EditPlayerModal extends Modal {

    private static final int MODAL_WIDTH = 200;
    private static final int MODAL_HEIGHT = 120;


    public EditPlayerModal(ClientActionHandler actionHandler, boolean isAddingPlayer) {
        super(
            actionHandler,
            isAddingPlayer ? "Add Player" : "Edit Player",
            MODAL_WIDTH,
            MODAL_HEIGHT
        );
    }

    



    
}

package online.refract.client.gui.modals;

import net.minecraft.network.chat.Component;
import online.refract.client.ClientActionHandler;
import online.refract.client.gui.Modal;


public class ResetModal extends Modal{


    public ResetModal(ClientActionHandler actionHandler) {
        super(
            actionHandler,
            "Warning, this will reset all scores", 
            200, 
            60
        );
    }

    @Override
    public void init(int screenWidth, int screenHeight) {
        super.init(screenWidth, screenHeight);
        addButtonRow(
            new ButtonData(Component.literal("Reset"), () -> {
                actionHandler.resetScores();
            }),
            new ButtonData(Component.literal("Cancel"), () -> {
                
            })
        );
    }



}
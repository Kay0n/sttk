package online.refract.client.gui.modals;

import net.minecraft.text.Text;
import online.refract.client.ClientActionHandler;


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
            createButtonDef(Text.literal("Reset"), () -> {
                actionHandler.resetScores();
            }),
            createButtonDef(Text.literal("Cancel"), () -> {
            })
        );
    }



}
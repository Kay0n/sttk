package online.refract.client.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import online.refract.Sttk;
import online.refract.client.ClientCoordinator;


public class ConfirmationModal extends Modal{

    private final String confirmButtonText;
    private final Runnable onConfirm;


    public ConfirmationModal(ClientCoordinator actionHandler, String title, String confirmButtonText, Runnable onConfirm) {
        super(actionHandler, title, 140, 5, 6);
        this.confirmButtonText = confirmButtonText;
        this.onConfirm = onConfirm;
    }

    @Override
    public void init(int screenWidth, int screenHeight, Font font) {
        super.init(screenWidth, screenHeight, font);


        addButtonRow(
            createButton(Component.literal(confirmButtonText), () -> {
                onConfirm.run();
                closeModal();
            }),
            createButton(
                Component.literal("Close"), () -> {
                    closeModal();
                }
            )
        );
        
    }




}

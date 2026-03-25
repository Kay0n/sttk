package online.refract.client.gui.grimiore;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import online.refract.client.ClientActionHandler;
import online.refract.client.gui.components.Modal;
import online.refract.game.state.ClocktowerPlayer;


public class TokenModal extends Modal{


    private ClocktowerPlayer selectedPlayer = null;
    private LinkPlayerModal linkPlayerModal;


    public TokenModal(ClientActionHandler actionHandler, LinkPlayerModal linkPlayerModal) {
        super(actionHandler, "", 140, 5, 6);
        this.linkPlayerModal = linkPlayerModal;
    }

    @Override
    public void init(int screenWidth, int screenHeight, Font font) {
        super.init(screenWidth, screenHeight, font);


        addButton(Component.literal("Private Chat"), () -> {
            closeModal();
        });

        addButton(Component.literal("Teleport to Player"), () -> {
            closeModal();
        });

        addButton(Component.literal("Teleport to House"), () -> {
            closeModal();
        });

        addButton(Component.literal("Start Vote"), () -> {
            closeModal();
        });

        addSpacerRow();

        addButton(Component.literal("Link Player"), () -> {
            this.linkPlayerModal.openModal(this.selectedPlayer);
            closeModal();
        });
        
    }

    public void openModal(ClocktowerPlayer player) {
        this.selectedPlayer = player;
        this.title = player.name;
        super.openModal();
    }


}

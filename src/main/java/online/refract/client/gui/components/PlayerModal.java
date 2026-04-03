package online.refract.client.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import online.refract.client.ClientActionHandler;
import online.refract.game.state.ClocktowerPlayer;


public class PlayerModal extends Modal{


    private ClocktowerPlayer player = null;
    private LinkPlayerModal linkPlayerModal;


    public PlayerModal(ClientActionHandler actionHandler, LinkPlayerModal linkPlayerModal) {
        super(actionHandler, "", 140, 5, 6);
        this.linkPlayerModal = linkPlayerModal;
    }

    @Override
    public void init(int screenWidth, int screenHeight, Font font) {
        super.init(screenWidth, screenHeight, font);


        addButton(Component.literal("Private Chat"), () -> {
            this.actionHandler.sendRequestPrivateChat(player);
            closeModal();
        });

        addButton(Component.literal("Teleport to Player"), () -> {
            this.actionHandler.sendRequestTeleportToPlayer(player);
            closeModal();
        });

        addButton(Component.literal("Teleport to House"), () -> {
            this.actionHandler.sendRequestTeleportToHouse(player);
            closeModal();
        });

        addSpacerRow();

        addButton(Component.literal("Link Player"), () -> {
            this.linkPlayerModal.openModal(this.player);
            closeModal();
        });
        
    }

    public void openModal(ClocktowerPlayer player) {
        this.player = player;
        this.title = player.name();
        super.openModal();
    }


}

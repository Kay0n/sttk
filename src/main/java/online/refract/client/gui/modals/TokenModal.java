package online.refract.client.gui.modals;

import net.minecraft.network.chat.Component;
import online.refract.client.ClientActionHandler;
import online.refract.client.gui.PlayerToken;
import online.refract.client.gui.objects.Modal;




public class TokenModal extends Modal{


    private PlayerToken selectedPlayer = null;
    private LinkPlayerModal linkPlayerModal;


    public TokenModal(ClientActionHandler actionHandler, LinkPlayerModal linkPlayerModal) {
        super(
            actionHandler,
            "", 
            140, 
            135,
            5,
            6
        );
        this.linkPlayerModal = linkPlayerModal;
    }

    @Override
    public void init(int screenWidth, int screenHeight) {
        super.init(screenWidth, screenHeight);



        addButton(Component.literal("Private Chat"), () -> {
            closeModal();
        });

        addButton(Component.literal("Teleport to Player"), () -> {
            closeModal();
        });

        addButton(Component.literal("Teleport to House"), () -> {
            closeModal();
        });

        addButton(Component.literal("Link Player"), () -> {
            this.linkPlayerModal.openModal();
            closeModal();
        });
        
    }

    
    public void openModal(PlayerToken player) {
        this.selectedPlayer = player;
        this.title = player.name;
        super.openModal();
    }


}


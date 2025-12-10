package online.refract.client.gui.modals;

import net.minecraft.network.chat.Component;
import online.refract.client.ClientActionHandler;
import online.refract.client.gui.PlayerToken;




public class TokenModal extends Modal{

    private static final int MODAL_WIDTH = 140;
    private static final int MODAL_HEIGHT = 145;  
    
    protected int MARGIN = 3;


    private PlayerToken selectedPlayer = null;


    public TokenModal(ClientActionHandler actionHandler) {
        super(
            actionHandler,
            "", 
            MODAL_WIDTH, 
            MODAL_HEIGHT
        );
    }

    @Override
    public void init(int screenWidth, int screenHeight) {
        super.init(screenWidth, screenHeight);

        this.modalMarginX = 10;
        this.modalMarginY = 10;
        this.elementMarginX = 5;
        this.elementMarginY = 1;
        addButton(createButtonDef(Component.literal("🏠 Home"), () -> actionHandler.homeTeleport(selectedPlayer.name)));
        addButton(createButtonDef(Component.literal("☠️ Kill/Revive"), () -> actionHandler.kill(selectedPlayer.name)));
        addButton(createButtonDef(Component.literal("👈 Nominate"), () -> actionHandler.nominate(selectedPlayer.name)));
        addButton(createButtonDef(Component.literal("🔨 On The Block"), () -> actionHandler.nominate(selectedPlayer.name)));
        addButton(createButtonDef(Component.literal("✅ Ghost Vote"), () -> actionHandler.nominate(selectedPlayer.name)));
    }


    public void openModal(PlayerToken player) {
        this.selectedPlayer = player;
        this.title = player.name;
        super.openModal();
    }


}


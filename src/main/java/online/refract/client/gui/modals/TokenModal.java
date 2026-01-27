package online.refract.client.gui.modals;

import net.minecraft.network.chat.Component;
import online.refract.client.ClientActionHandler;
import online.refract.client.gui.Modal;
import online.refract.client.gui.PlayerToken;




public class TokenModal extends Modal{

    private static final int MODAL_WIDTH = 140;
    private static final int MODAL_HEIGHT = 152;  
    
    protected int MARGIN = 3;


    private PlayerToken selectedPlayer = null;


    public TokenModal(ClientActionHandler actionHandler) {
        super(
            actionHandler,
            "", 
            MODAL_WIDTH, 
            MODAL_HEIGHT,
            5,
            10
        );
    }

    @Override
    public void init(int screenWidth, int screenHeight) {
        super.init(screenWidth, screenHeight);

        // this.MODAL_MARGIN_X = 10;
        // this.MODAL_MARGIN_Y = 10;
        // this.ELEMENT_MARGIN_X = 5;
        // this.ELEMENT_MARGIN_Y = 1;

        // addButton(Component.literal("🏠 Home"), () -> actionHandler.homeTeleport(selectedPlayer.name));
        // addButton(Component.literal("☠️ Kill/Revive"), () -> actionHandler.kill(selectedPlayer.name));
        // addButton(Component.literal("👈 Nominate"), () -> actionHandler.nominate(selectedPlayer.name));
        // addButton(Component.literal("🔨 On The Block"), () -> actionHandler.nominate(selectedPlayer.name));
        // addButton(Component.literal("✅ Ghost Vote"), () -> actionHandler.nominate(selectedPlayer.name));
        addButton(Component.literal("🏠 Home"), () -> actionHandler.debug("Home: " + selectedPlayer.name));
        addButton(Component.literal("☠️ Kill/Revive"), () -> actionHandler.debug("Kill/Revive: " + selectedPlayer.name));
        addButton(Component.literal("👈 Nominate"), () -> actionHandler.debug("Nominate: " + selectedPlayer.name));
        addButton(Component.literal("🔨 On The Block"), () -> actionHandler.debug("On The Block: " + selectedPlayer.name));
        addButton(Component.literal("✅ Ghost Vote"), () -> actionHandler.debug("Ghost Vote: " + selectedPlayer.name));
    }

    

    public void openModal(PlayerToken player) {
        this.selectedPlayer = player;
        this.title = player.name;
        super.openModal();
    }


}


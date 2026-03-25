package online.refract.client.gui.grimiore;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import online.refract.client.ClientActionHandler;
import online.refract.client.gui.components.Modal;

public class LinkPlayerModal extends Modal {

    private PlayerSelectionWidget playerList;

    public LinkPlayerModal(ClientActionHandler actionHandler) {
        super(actionHandler, "Link Player", 200);
    }

    @Override
    public void init(int screenWidth, int screenHeight, Font font) {
        super.init(screenWidth, screenHeight, font);

        this.playerList = new PlayerSelectionWidget(Minecraft.getInstance());

        this.playerList.addPlayer("Sam");
        this.playerList.addPlayer("Max");
        this.playerList.addPlayer("Ellie");
        this.playerList.addPlayer("Sarah");
        this.playerList.addPlayer("Lily");
        this.playerList.addPlayer("Kym");
        this.playerList.addPlayer("Bob");
        this.playerList.addPlayer("Alice");
        this.playerList.addPlayer("Charlie");
        this.playerList.addPlayer("Sam");
        this.playerList.addPlayer("Max");
        this.playerList.addPlayer("Ellie");
        this.playerList.addPlayer("Sarah");
        this.playerList.addPlayer("Lily");
        this.playerList.addPlayer("Kym");
        this.playerList.addPlayer("Bob");
        this.playerList.addPlayer("Alice");
        this.playerList.addPlayer("Charlie");
        this.playerList.addPlayer("None");

        addCustomRow(100, this.playerList);


        


        addButtonRow(
            createButton(
                Component.literal("Link"), () -> {
                    if (playerList.getSelected() != null) {
                        this.actionHandler.debug("Linked: " + playerList.getSelected().getName());
                    }
                    closeModal();
                }
            ),
            createButton(
                Component.literal("unlink"), () -> {
                    if (playerList.getSelected() != null) {
                        this.actionHandler.debug("Unlinked: " + playerList.getSelected().getName());
                    }
                    closeModal();
                }
            )
        );
    }

    
}
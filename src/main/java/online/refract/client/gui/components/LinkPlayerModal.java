package online.refract.client.gui.components;

import java.util.Collection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import online.refract.client.ClientActionHandler;
import online.refract.game.state.ClocktowerPlayer;

public class LinkPlayerModal extends Modal {

    private SelectionWidget<String> playerListWidget;
    private ClocktowerPlayer linkingPlayer;

    public LinkPlayerModal(ClientActionHandler actionHandler) {
        super(actionHandler, "Link Player", 250);
    }



    @Override
    public void init(int screenWidth, int screenHeight, Font font) {
        super.init(screenWidth, screenHeight, font);

        this.playerListWidget = new SelectionWidget<>(name -> name);

        


        addCustomRow(100, this.playerListWidget);


        addButtonRow(
            createButton(
                Component.literal("Link"), () -> {
                    if (linkingPlayer != null) {
                        String selectedPlayerName = playerListWidget.getSelectedValue();
                        if (selectedPlayerName == null) {
                            this.actionHandler.debug("No player selected");
                            return;
                        }
                        this.actionHandler.sendLinkUsername(linkingPlayer, selectedPlayerName);
                        this.actionHandler.debug("Linking player: " + linkingPlayer.name() + " with username: " + selectedPlayerName);
                    }
                    closeModal();
                }
            ),
            createButton(
                Component.literal("Close"), () -> {
                    closeModal();
                }
            )
        );
    }




    @Override    
    public void openModal() {
        throw new RuntimeException("Use openModal(ClocktowerPlayer player) instead");
    }



    public void openModal(ClocktowerPlayer player) {
        this.linkingPlayer = player;
        this.playerListWidget.clearEntries();

        Minecraft mc = Minecraft.getInstance();

        if (mc.getConnection() == null) {
            this.actionHandler.debug("No connection - cannot fetch player list");
            return;
        }

        Collection<PlayerInfo> players = mc.getConnection().getOnlinePlayers();

        for (PlayerInfo info : players) {
            this.playerListWidget.addEntry(info.getProfile().getName());
        }



        super.openModal();
    }
        
}



package online.refract.client.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import online.refract.client.ClientCoordinator;
import online.refract.game.state.ClocktowerPlayer;
import online.refract.game.state.ClocktowerState;

public class VoteModal extends Modal {

    private SelectionWidget<ClocktowerPlayer> playerListWidget;

    public VoteModal(ClientCoordinator clientCoordinator) {
        super(clientCoordinator, "Vote", 250);
    }



    @Override
    public void init(int screenWidth, int screenHeight, Font font) {
        super.init(screenWidth, screenHeight, font);

        this.playerListWidget = new SelectionWidget<>(ClocktowerPlayer::name);
        
        addCustomRow(100, this.playerListWidget);


        addButtonRow(
            createButton(
                Component.literal("Start Vote"), () -> {
                    ClocktowerPlayer selectedPlayer = playerListWidget.getSelectedValue();
                    if (selectedPlayer == null) {
                        return;
                    }
                    this.actionHandler.startVoteForPlayer(selectedPlayer);
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
    @Deprecated(forRemoval = true)
    public void openModal() {
        throw new RuntimeException("Use openModal(ClocktowerState state) instead");
    }



    public void openModal(ClocktowerState state) {
        this.playerListWidget.clearEntries();

        for (ClocktowerPlayer player : state.players()) {
            this.playerListWidget.addEntry(player);
        }

        super.openModal();
    }
        
}



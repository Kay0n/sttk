package online.refract.client.gui.components;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;
import online.refract.client.ClientCoordinator;
import online.refract.game.state.ClocktowerPlayer;


public class PlayerModal extends Modal{

    private LinkPlayerModal linkPlayerModal;
    private ClocktowerPlayer player;
    private boolean playerVersion;

    
    public PlayerModal(ClientCoordinator actionHandler, LinkPlayerModal linkPlayerModal, boolean isPlayerVersion) {
        super(actionHandler, "", 140, 5, 6);
        this.linkPlayerModal = linkPlayerModal;
        this.playerVersion = isPlayerVersion;
    }


    private void buildWidgets(){
        if (player.linkedMinecraftUsername() != null) {
            addLabelRow(player.linkedMinecraftUsername(), 0xFF919191, 0.75f);
        }

        if (playerVersion){
            addButton(Component.literal("Close"), () -> {
                closeModal();
            });
            return;
        }

        addButton(Component.literal("Private Chat"), () -> {
            this.actionHandler.requestPrivateChat(player);
            closeModal();
        });

        addButton(Component.literal("Teleport to Player"), () -> {
            this.actionHandler.requestTeleportToPlayer(player);
            closeModal();
        });

        addButton(Component.literal("Teleport to House"), () -> {
            this.actionHandler.requestTeleportToHouse(player);
            closeModal();
        });

        addSpacerRow();

        addButton(Component.literal("Link Player"), () -> {
            this.linkPlayerModal.openModal(player);
            closeModal();
        });
    }


    public void openModal(ClocktowerPlayer player) {
        this.player = player;
        this.title = player.name();
        clearWidgets();
        buildWidgets();
        super.openModal();
    }


    public @Nullable ClocktowerPlayer getPlayer(){
        return player;
    }


    @Override
    @Deprecated
    public void openModal() {
        throw new RuntimeException("Use openModal(ClocktowerPlayer player) instead");
    }


}

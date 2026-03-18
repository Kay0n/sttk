package online.refract.client.gui;
import online.refract.Sttk;
import online.refract.client.ClientActionHandler;
import online.refract.client.SttkClient;
import online.refract.client.gui.GuiScale.MouseCoords;
import online.refract.client.gui.modals.LinkPlayerModal;
import online.refract.client.gui.modals.TokenModal;
import online.refract.client.gui.modals.TownModal;
import online.refract.client.gui.objects.Modal;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;



public class GrimoireScreen extends Screen {


    private final ClientActionHandler actionHandler = new ClientActionHandler();

    private final List<PlayerToken> players = new ArrayList<>();
    private final List<Button> globalButtons = new ArrayList<>();
    private final List<Modal> modals = new ArrayList<>();
    private final TokenRenderer tokenRenderer = new TokenRenderer();

    private final LinkPlayerModal linkPlayerModal = registerModal(new LinkPlayerModal(actionHandler));
    private final TokenModal tokenModal = registerModal(new TokenModal(actionHandler, linkPlayerModal));
    private final TownModal townModal = registerModal(new TownModal(actionHandler));





    public GrimoireScreen() {
        super(Component.nullToEmpty("Grimoire"));
        for (int i = 0; i < Sttk.SERVER_PLAYER_COUNT; i++) {
            players.add(new PlayerToken(i + 1, "Player " + (i + 1), "User" + (i + 1)));
        }
    }



    
    @Override
    protected void init() {
        this.globalButtons.clear();
        setupGlobalButtons();

        for (Modal modal : modals) {
            modal.init(this.width, this.height, this.font);
        }

        super.init();
    }



    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {   
        
        boolean modalsAreOpen = modals.stream().anyMatch(Modal::isOpen);

        for (Button btn : globalButtons) {
            btn.active = !modalsAreOpen;
            btn.render(context, mouseX, mouseY, delta);
        }

        GuiScale.disableGuiScale(context);

        tokenRenderer.calculateTokenLayout(players.size(), GuiScale.getUnscaledWidth(), GuiScale.getUnscaledHeight());
        tokenRenderer.drawTokenCircle(context, this.font, players, GuiScale.getUnscaledWidth(), GuiScale.getUnscaledHeight());

        GuiScale.enableGuiScale(context);

        for (Modal modal : modals) {
            modal.render(context, mouseX, mouseY, delta);
        }
    }



    private void setupGlobalButtons() {
        int btnWidth = 70;
        int padding = 2;
        int screenWidth = this.width; 
        int x = padding;
        int y = padding;
        int gap = 22;
        

        addGlobalBtn("Distribute Roles", screenWidth - btnWidth - (padding * 2), this.height - y - 40, () -> townModal.openModal());
        addGlobalBtn("🏘 Town", screenWidth - btnWidth - (padding * 2), this.height - y - 20, () -> townModal.openModal());

    }


    


    private void addGlobalBtn(String label, int x, int y, Runnable action) {
        Button btn = Button.builder(Component.nullToEmpty(label), b -> action.run())
                .bounds(x, y, 70, 20).build();
        this.globalButtons.add(btn);
    }


    private <T extends Modal> T registerModal(T modal) {
        this.modals.add(modal);
        return modal;
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        for (Modal modal : modals) {
            if (modal.mouseClicked((int) mouseX, (int) mouseY, button)) {
                return true;
            }
        }

        for (Button btn : globalButtons) {
            if (btn.mouseClicked(mouseX, mouseY, button)) return true;
        }
        MouseCoords unscaledMouse = GuiScale.getUnscaledMouseCoords(mouseX, mouseY);

        PlayerToken selectedPlayer = tokenRenderer.handleTokenClick(players, unscaledMouse.x(), unscaledMouse.y(), button);
        if (selectedPlayer != null) {
            tokenModal.openModal(selectedPlayer);
            return true;
        }
        return false; 
    }


    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (Modal modal : modals) {
            if (modal.mouseReleased(mouseX, mouseY, button)) return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        for (Modal modal : modals) {
            if (modal.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        for (Modal modal : modals) {
            if (modal.mouseDragged(mouseX, mouseY, button, dragX, dragY)) return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        for (Modal modal : modals) {
            if (modal.charTyped(chr, modifiers)) return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (SttkClient.OPEN_GRIMOIRE_KEY.matches(keyCode, scanCode)) {
            this.onClose();
            return true;   
        }

        for (Modal modal : modals) {
            if (modal.keyPressed(keyCode, scanCode, modifiers)) return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}

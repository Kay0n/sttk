package online.refract.client.gui.grimiore;

import online.refract.client.ClientActionHandler;
import online.refract.client.SttkClient;
import online.refract.client.ClocktowerClientState;
import online.refract.client.gui.components.Modal;
import online.refract.game.state.ClocktowerPlayer;
import online.refract.game.state.ClocktowerState;
import online.refract.game.state.Enums.TownConnectionStatus;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class GrimoireScreen extends Screen {

    private final ClientActionHandler actionHandler = new ClientActionHandler();
    private final TokenRenderer tokenRenderer = new TokenRenderer();
    private final List<Button> globalButtons = new ArrayList<>();
    private final List<Modal> modals = new ArrayList<>();

    private final LinkPlayerModal linkPlayerModal = registerModal(new LinkPlayerModal(actionHandler));
    private final TokenModal tokenModal = registerModal(new TokenModal(actionHandler, linkPlayerModal));
    private final TownModal townModal = registerModal(new TownModal(actionHandler));



    public GrimoireScreen() {
        super(Component.nullToEmpty("Grimoire"));
    }



    public void onStateUpdated() {
        if (townModal.isOpen()) {
            townModal.updateComponents();
        }
        rebuildButtons();
    }



    private void rebuildButtons() {
        globalButtons.clear();
        ClocktowerState state = ClocktowerClientState.getState();
        TownConnectionStatus status = state.townConnectionStatus();

        globalButtons.add(makeButton("🏘 Town", width - 74, height - 24, () -> townModal.openModal()));

        if (status == TownConnectionStatus.CONNECTED) {

            // TODO: confirmation modal, add "text" widget to Modal class
            globalButtons.add(makeButton("Send Roles", width - 74, height - 24 - (22 * 1), () -> {}));

            // TODO: send packet
            globalButtons.add(makeButton("Start Vote", width - 74, 2, () -> {}));

        } 

        globalButtons.forEach(this::addRenderableWidget);
    }



    @Override
    protected void init() {
        rebuildButtons();
        modals.forEach(m -> m.init(width, height, font));
        super.init();
    }



    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float delta) {
        boolean anyModalOpen = modals.stream().anyMatch(Modal::isOpen);
        globalButtons.forEach(b -> b.active = !anyModalOpen);

        tokenRenderer.render(gfx, font, ClocktowerClientState.getState().players(), width, height);

        modals.forEach(m -> m.render(gfx, mouseX, mouseY, delta));
        super.render(gfx, mouseX, mouseY, delta);
    }



    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Modal m : modals)
            if (m.mouseClicked((int) mouseX, (int) mouseY, button)) return true;

        for (Button b : globalButtons)
            if (b.mouseClicked(mouseX, mouseY, button)) return true;

        if (button == 0) {
            ClocktowerPlayer clickedPlayer = tokenRenderer.hitTest(ClocktowerClientState.getState().players(), mouseX, mouseY, width, height);
            if (clickedPlayer != null) {
                tokenModal.openModal(clickedPlayer);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (Modal m : modals)
            if (m.mouseReleased(mouseX, mouseY, button)) return true;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        for (Modal m : modals)
            if (m.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) return true;
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        for (Modal m : modals)
            if (m.mouseDragged(mouseX, mouseY, button, dragX, dragY)) return true;
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        for (Modal m : modals)
            if (m.charTyped(chr, modifiers)) return true;
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (SttkClient.OPEN_GRIMOIRE_KEY.matches(keyCode, scanCode)) {
            onClose();
            return true;
        }
        for (Modal m : modals)
            if (m.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private Button makeButton(String label, int x, int y, Runnable action) {
        return Button.builder(Component.nullToEmpty(label), b -> action.run())
                .bounds(x, y, 70, 20).build();
    }

    private <T extends Modal> T registerModal(T modal) {
        modals.add(modal);
        return modal;
    }
}


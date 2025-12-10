package online.refract.client.gui;

import online.refract.Sttk;
import online.refract.client.ClientActionHandler;
import online.refract.client.SttkClient;
import online.refract.client.gui.GuiScale.MouseCoords;
import online.refract.client.gui.modals.OrderModal;
import online.refract.client.gui.modals.ResetModal;
import online.refract.client.gui.modals.TimerModal;
import online.refract.client.gui.modals.TokenModal;

import java.util.ArrayList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;



public class GrimoireScreen extends Screen {


    private final ClientActionHandler actionHandler = new ClientActionHandler();

    private final ArrayList<PlayerToken> players = new ArrayList<>();
    private final ArrayList<Button> globalButtons = new ArrayList<>();
    private final TokenRenderer tokenRenderer = new TokenRenderer();

    private final TokenModal tokenModal = new TokenModal(actionHandler);
    private final OrderModal orderModal = new OrderModal(actionHandler);
    private final ResetModal resetModal = new ResetModal(actionHandler);
    private final TimerModal timerModal = new TimerModal(actionHandler);





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
        resetModal.init(this.width, this.height);
        orderModal.init(this.width, this.height);
        tokenModal.init(this.width, this.height);
        timerModal.init(this.width, this.height);
        super.init();
    }



    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {   
        
        boolean modalsAreOpen = resetModal.isOpen() || orderModal.isOpen() || tokenModal.isOpen();

        for (Button btn : globalButtons) {
            btn.active = !modalsAreOpen;
            btn.render(context, mouseX, mouseY, delta);
        }

        GuiScale.disableGuiScale(context);

        tokenRenderer.calculateTokenLayout(players.size(), GuiScale.getUnscaledWidth(), GuiScale.getUnscaledHeight());
        tokenRenderer.drawTokenCircle(context, font, players, GuiScale.getUnscaledWidth(), GuiScale.getUnscaledHeight());

        GuiScale.enableGuiScale(context);

        orderModal.render(context, font, mouseX, mouseY, delta);
        resetModal.render(context, font, mouseX, mouseY, delta);
        tokenModal.render(context, font, mouseX, mouseY, delta);
        timerModal.render(context, font, mouseX, mouseY, delta);
    }



    private void setupGlobalButtons() {
        int btnWidth = 70;
        int padding = 2;
        int screenWidth = this.width; 
        int x = padding;
        int y = padding;
        int gap = 22;
        
        addGlobalBtn("✋ Vote", x, y, () -> actionHandler.startVote());
        addGlobalBtn("🌘 Night", x, y + gap, () -> actionHandler.setNight());
        addGlobalBtn("☁ Evening", x, y + gap*2, () -> actionHandler.setEvening());
        addGlobalBtn("🔆 Day", x, y + gap*3, () -> actionHandler.setDay());

        addGlobalBtn("⏳ Timer", screenWidth - btnWidth - padding * 2, y, () -> timerModal.openModal());
        addGlobalBtn("☰ Order",(screenWidth - btnWidth - (padding * 2)), y + gap, () -> orderModal.openModal(players));
        addGlobalBtn("🔄 Reset", (screenWidth - btnWidth - (padding * 2)), y + gap*2, () -> resetModal.openModal());
    }



    private void addGlobalBtn(String label, int x, int y, Runnable action) {
        Button btn = Button.builder(Component.nullToEmpty(label), b -> action.run())
                .bounds(x, y, 70, 20).build();
        this.globalButtons.add(btn);
    }

    

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if (tokenModal.mouseClicked((int) mouseX, (int) mouseY, button)){
            return true;
        } 
        if (orderModal.mouseClicked((int) mouseX, (int) mouseY, button)){
            return true;
        }
        if(resetModal.mouseClicked((int) mouseX, (int) mouseY, button)){
            return true;
        }
        if(timerModal.mouseClicked((int) mouseX, (int) mouseY, button)){
            return true;
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
        if (orderModal.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        return false;
    }



    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (timerModal.charTyped(chr, modifiers)){
            return true;
        }
        return false;
    }



    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (SttkClient.OPEN_GRIMOIRE_KEY.matches(keyCode, scanCode)) {
            this.onClose();
            return true;   
        }

        if (tokenModal.keyPressed(keyCode, scanCode, modifiers)){ return true; }
        if (orderModal.keyPressed(keyCode, scanCode, modifiers)){ return true; }
        if (resetModal.keyPressed(keyCode, scanCode, modifiers)){ return true; }
        if(timerModal.keyPressed(keyCode, scanCode, modifiers)){ return true;}

        return super.keyPressed(keyCode, scanCode, modifiers);
    }



    

}
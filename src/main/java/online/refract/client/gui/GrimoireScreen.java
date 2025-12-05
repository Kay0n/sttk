package online.refract.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import online.refract.Sttk;
import online.refract.client.SttkClient;
import online.refract.client.gui.modals.OrderModal;
import online.refract.client.gui.modals.TokenModal;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix3x2fStack;

public class GrimoireScreen extends Screen {


    private static final float BASE_RESOLUTION_HEIGHT = 540f * 0.64f;


    private final ArrayList<PlayerToken> players = new ArrayList<>();
    private final ArrayList<ButtonWidget> globalButtons = new ArrayList<>();

    private final TokenRenderer tokenRenderer = new TokenRenderer();
    private final TokenModal tokenModal = new TokenModal();
    private final OrderModal orderModal = new OrderModal();




    public GrimoireScreen() {
        super(Text.of("Grimoire"));
        for (int i = 0; i < Sttk.SERVER_PLAYER_COUNT; i++) {
            players.add(new PlayerToken(i + 1, "Player " + (i + 1), "User" + (i + 1)));
        }
    }


    
    @Override
    protected void init() {
        this.globalButtons.clear();
        setupGlobalButtons();
    }



    private float getDynamicScale() {
        int physHeight = MinecraftClient.getInstance().getWindow().getFramebufferHeight();
        if (physHeight == 0) return 1.0f;
        return physHeight / BASE_RESOLUTION_HEIGHT;
    }
    private int getVirtualWidth() {
        return (int) (MinecraftClient.getInstance().getWindow().getFramebufferWidth() / getDynamicScale());
    }
    private int getVirtualHeight() {
        return (int) (MinecraftClient.getInstance().getWindow().getFramebufferHeight() / getDynamicScale());
    }



    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // scale matrix to bypass gui scale
        float dynamicScale = getDynamicScale();
        double guiScale = MinecraftClient.getInstance().getWindow().getScaleFactor();
        int virtualMouseX = (int)((mouseX * guiScale) / dynamicScale);
        int virtualMouseY = (int)((mouseY * guiScale) / dynamicScale);
        context.getMatrices().pushMatrix();
        float matrixScale = (float) (dynamicScale / guiScale);
        context.getMatrices().scale(matrixScale, matrixScale);

        for (ButtonWidget btn : globalButtons) {
            btn.render(context, virtualMouseX, virtualMouseY, delta);
        }

        tokenRenderer.calculateTokenLayout(players.size(), getVirtualWidth(), getVirtualHeight());
        tokenRenderer.drawTokenCircle(context, textRenderer, players, virtualMouseX, virtualMouseY, getVirtualWidth(), getVirtualHeight());

        tokenModal.renderModal(context, textRenderer, virtualMouseX, virtualMouseY, delta, getVirtualWidth(), getVirtualHeight());
        orderModal.render(context, textRenderer, virtualMouseX, virtualMouseY, delta, getVirtualWidth(), getVirtualHeight());
        
        context.getMatrices().popMatrix();
    }



    private void setupGlobalButtons() {
        int btnWidth = 70;
        int padding = 2;
        int screenWidth = getVirtualWidth(); 
        int x = padding;
        int y = padding;
        int gap = 22;
        
        addGlobalBtn("✋ Vote", x, y, "Action: Vote");
        addGlobalBtn("🌘 Night", x, y + gap, "Action: Night");
        addGlobalBtn("☁️ Evening", x, y + gap*2, "Action: Evening");
        addGlobalBtn("🔆 Day", x, y + gap*3, "Action: Day");

        addGlobalBtn("⏳ Timer", screenWidth - btnWidth - padding * 2, y, "Action: Timer");
        addGlobalBtn("☰ Order",(screenWidth - btnWidth - (padding * 2)), y + gap, "Action: Order");
        addGlobalBtn("🔄 Reset", (screenWidth - btnWidth - (padding * 2)), y + gap*2, "Action: Reset Scores");
    }



    private void addGlobalBtn(String label, int x, int y, String debugMsg) {
        ButtonWidget btn = ButtonWidget.builder(Text.of(label), b -> debug(debugMsg))
                .dimensions(x, y, 70, 20).build();
        this.globalButtons.add(btn);
    }

    

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double mcScale = MinecraftClient.getInstance().getWindow().getScaleFactor();
        float dynamicScale = getDynamicScale();
        double virtualX = (mouseX * mcScale) / dynamicScale;
        double virtualY = (mouseY * mcScale) / dynamicScale;

        // global buttons
        for (ButtonWidget btn : globalButtons) {
            if (btn.mouseClicked(virtualX, virtualY, button)) return true;
        }

        // modals
        if (tokenModal.handleModalClicked((int) virtualX, (int) virtualY, button, getVirtualWidth(), getVirtualHeight())){
            return true;
        } 
        if (orderModal.mouseClicked((int) virtualX, (int) virtualY, button, getVirtualWidth(), getVirtualHeight())){
            return true;
        }

        // token button
        PlayerToken selectedPlayer = tokenRenderer.handleTokenClick(players, virtualX, virtualY, button);
        if (selectedPlayer != null) {
            tokenModal.openModal(selectedPlayer, getVirtualWidth(), getVirtualHeight());
            return true;
        }

        return false; 
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        double mcScale = MinecraftClient.getInstance().getWindow().getScaleFactor();
        float dynamicScale = getDynamicScale();
        double virtualX = (mouseX * mcScale) / dynamicScale;
        double virtualY = (mouseY * mcScale) / dynamicScale;

        if (orderModal.mouseReleased((int) virtualX, (int) virtualY, button, getVirtualWidth(), getVirtualHeight())){
            return true;
        }

        return false;
    }



    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (SttkClient.OPEN_GRIMOIRE_KEY.matchesKey(keyCode, scanCode)) {
            this.close();
            return true;   
        }

        if (tokenModal.keyPressed(keyCode, scanCode, modifiers)){ return true; }
        if (orderModal.keyPressed(keyCode, scanCode, modifiers)){ return true; }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }








    // ====== Utils ======
    private void debug(String msg) {
        if (client != null && client.player != null) {
            client.player.sendMessage(Text.of("§b[Grimoire] §f" + msg), false);
            if (msg == "Action: Order") {
                orderModal.openModal(players);
            }
        }
            

    }
    

}
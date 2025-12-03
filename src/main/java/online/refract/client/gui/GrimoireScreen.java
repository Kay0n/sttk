
package online.refract.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import online.refract.Sttk;
import online.refract.client.SttkClient;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix3x2fStack;


public class GrimoireScreen extends Screen {

    private static final Identifier TOKEN_TEXTURE = Identifier.of("sttk", "textures/gui/token-greyscale.png");

    private final List<PlayerToken> players = new ArrayList<>();
    private PlayerToken selectedPlayer = null;

    private static final int MAX_TOKEN_SIZE = 400; // Never bigger than this
    private static final int TOKEN_MARGIN = 1;    // Pixels of space between tokens
    private static final int SCREEN_PADDING = 1; // Pixels from screen edge to token edge

    private int currentTokenSize = 32;
    private int currentLayoutRadius = 100;

    private final List<ButtonWidget> globalButtons = new ArrayList<>();
    private final List<ButtonWidget> popupButtons = new ArrayList<>();

    public GrimoireScreen() {
        super(Text.of("Grimoire"));
        for (int i = 0; i < Sttk.SERVER_PLAYER_COUNT; i++) {
            players.add(new PlayerToken(i + 1, "Player " + (i + 1), "User" + (i + 1)));
        }
    }


    @Override
    protected void init() {
        this.globalButtons.clear();
        this.popupButtons.clear();
        this.selectedPlayer = null;
        setupGlobalButtons();
    }

    private void calculateLayout() {
        if (players.size() == 0) return;

        int maxScreenTokenSize = MAX_TOKEN_SIZE / MinecraftClient.getInstance().getWindow().getScaleFactor();

        double maxScreenRadius = (Math.min(this.width, this.height) / 2.0) - SCREEN_PADDING;

        if (players.size() == 1) {
            this.currentTokenSize = maxScreenTokenSize;
            this.currentLayoutRadius = 0;
            return;
        }

        double sinN = Math.sin(Math.PI / players.size());
        double maxFeasibleSize = (2 * maxScreenRadius * sinN - TOKEN_MARGIN) / (1 + sinN);

        int calculatedSize = (int) Math.min(maxScreenTokenSize, maxFeasibleSize);
        this.currentTokenSize = Math.max(10, calculatedSize); // 10 is a sanity "absolute minimum" so it doesn't vanish        // 4. Back-calculate the Layout Radius

        this.currentLayoutRadius = (int) (maxScreenRadius - (this.currentTokenSize / 2.0));
        
        if (this.currentLayoutRadius < 0) this.currentLayoutRadius = 0;
    }



    private void setupGlobalButtons() {
        int btnWidth = 70;
        int btnHeight = 20;
        int padding = 4;
        int x = 0 + padding;
        int y = padding;
        int gap = 22;
        
        addGlobalBtn("✋ Vote", x, y, "Action: Vote");
        addGlobalBtn("🌘 Night", x, y + gap, "Action: Night");
        addGlobalBtn("☁️ Evening", x, y + gap*2, "Action: Evening");
        addGlobalBtn("🔆 Day", x, y + gap*3, "Action: Day");

        addGlobalBtn("⏳ Timer", x + this.width - btnWidth - padding * 2, y, "Action: Timer");
        addGlobalBtn("☰ Order",(x + this.width - btnWidth - (padding * 2)), y + gap, "Action: Rearrange");
        addGlobalBtn("🔄 Reset", (x + this.width - btnWidth - (padding * 2)), y + gap*2, "Action: Reset Scores");
    }


    private void addGlobalBtn(String label, int x, int y, String debugMsg) {
        ButtonWidget btn = ButtonWidget.builder(Text.of(label), b -> debug(debugMsg))
                .dimensions(x, y, 70, 20).build();
        this.addDrawableChild(btn);
        this.globalButtons.add(btn);
    }




    private void openPopup(PlayerToken player) {
        this.selectedPlayer = player;
        globalButtons.forEach(b -> b.active = false);
        
        ButtonWidget closeBtn = ButtonWidget.builder(Text.of("❌ Close"), b -> closePopup())
                .dimensions((this.width/2)-60, (this.height/2)+40, 120, 20).build();
        this.addDrawableChild(closeBtn);
        this.popupButtons.add(closeBtn);
    }


    private void closePopup() {
        this.selectedPlayer = null;
        this.popupButtons.forEach(this::remove);
        this.popupButtons.clear();
        globalButtons.forEach(b -> b.active = true);
    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        calculateLayout(); 

        drawPlayerCircle(context, mouseX, mouseY);

        if (selectedPlayer != null) {
            drawPopupOverlay(context);
        }
    }






    private void drawPlayerCircle(DrawContext context, int mouseX, int mouseY) {
        if (players.isEmpty()) return;

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        double startAngle = -Math.PI / 2; // first token at top

        for (int i = 0; i < players.size(); i++) {
            PlayerToken player = players.get(i);

            double angle = startAngle + (i * (2 * Math.PI / players.size()));

            int x = (int) (centerX + currentLayoutRadius * Math.cos(angle));
            int y = (int) (centerY + currentLayoutRadius * Math.sin(angle));

            player.renderX = x;
            player.renderY = y;

            int drawX = x - (currentTokenSize / 2);
            int drawY = y - (currentTokenSize / 2);

            drawToken(context, drawX, drawY, currentTokenSize, mouseX, mouseY, player);
        }

        for (int i = 0; i < players.size(); i++) {
            PlayerToken player = players.get(i);

            int drawX = player.renderX - (currentTokenSize / 2);
            int drawY = player.renderY - (currentTokenSize / 2);

            drawText(context, drawX, drawY, currentTokenSize, mouseX, mouseY, player);
        }
    }



    private void drawToken(DrawContext context, int x, int y, int size, int mouseX, int mouseY, PlayerToken player) {
        // boolean isHovered = false;
        // if (selectedPlayer == null) {
        //     double dist = Math.sqrt(Math.pow(mouseX - x, 2) + Math.pow(mouseY - y, 2));
        //     if (dist <= currentTokenSize / 2.0) isHovered = true;
        // }

        context.drawTexture(
                RenderPipelines.GUI_TEXTURED, 
                TOKEN_TEXTURE,
                x, y,
                0f, 0f,
                currentTokenSize, currentTokenSize,
                currentTokenSize, currentTokenSize
        );



        
    }

    public void drawText(DrawContext context, int x, int y, int size, int mouseX, int mouseY, PlayerToken player){
        float referenceSize = 64.0f; 
        float scale = size / referenceSize;

        Matrix3x2fStack matrices = context.getMatrices();
        matrices.pushMatrix();

        float centerX = x + (size / 2.0f);
        

        float paddingY = size * 0.05f; 
        float anchorY = y + paddingY;

        matrices.translate(centerX, anchorY);
        
        matrices.scale(scale, scale);

        Text name = Text.of(player.name);
        int nameW = this.textRenderer.getWidth(name);
        int textX = -nameW / 2;
        int textY = 0;
        context.drawTextWithShadow(this.textRenderer, name, textX , textY, 0xFFFFAA00);
        matrices.popMatrix();
    }


    private void drawPopupOverlay(DrawContext context) {
        context.fillGradient(0, 0, this.width, this.height, 0xAA000000, 0xAA000000);
        int w = 140; int h = 100;
        int x = (this.width / 2) - (w/2);
        int y = (this.height / 2) - (h/2);
        context.fill(x, y, x + w, y + h, 0xFF202020);
        context.drawBorder(x, y, w, h, 0xFFFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.of(selectedPlayer.name), this.width/2, y + 6, 0xFFFFFFFF);
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (selectedPlayer != null) return super.mouseClicked(mouseX, mouseY, button);

        if (button == 0) {
            for (PlayerToken player : players) {
                double dist = Math.sqrt(Math.pow(mouseX - player.renderX, 2) + Math.pow(mouseY - player.renderY, 2));
                // Use dynamic size for hitbox
                if (dist <= (currentTokenSize / 2.0)) {
                    openPopup(player);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (SttkClient.OPEN_GRIMOIRE_KEY.matchesKey(keyCode, scanCode)) {
            this.close();
            return true;   
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }


    private void debug(String msg) {
        if (client != null && client.player != null) 
            client.player.sendMessage(Text.of("§b[Grimoire] §f" + msg), false);
    }
    

    private static class PlayerToken {
        String name, username;
        int renderX, renderY;
        public PlayerToken(int id, String name, String username) {
            this.name = name; this.username = username;
        }
    }
}
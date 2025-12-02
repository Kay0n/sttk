
package online.refract.client;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import online.refract.Sttk;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.pipeline.RenderPipeline;

public class GrimoireScreen extends Screen {

    private static final Identifier TOKEN_TEXTURE = Identifier.of("sttk", "textures/gui/token-greyscale.png");

    private final List<PlayerToken> players = new ArrayList<>();
    private PlayerToken selectedPlayer = null;

    private static final int MAX_TOKEN_SIZE = 80; // Never bigger than this
    private static final int MIN_TOKEN_SIZE = 32; // Never smaller than this
    private static final int TOKEN_BUFFER = 5;    // Pixels of space between tokens

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
        
        calculateLayout();
    }


    private void calculateLayout() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        int maxAvailableRadius = (Math.min(this.width, this.height) / 2) - 40;

        // Formula: Chord Length = 2 * R * sin(PI / N)
        // Goal: TokenSize + Buffer <= Chord Length
        // Therefore: TokenSize = (2 * R * sin(PI / N)) - Buffer
        double angleStep = Math.PI / players.size(); // (PI / N)
        double maxAllowedSizeByCircumference = (2 * maxAvailableRadius * Math.sin(angleStep)) - TOKEN_BUFFER;

        this.currentTokenSize = (int) Math.clamp(maxAllowedSizeByCircumference, MIN_TOKEN_SIZE, MAX_TOKEN_SIZE);

        this.currentLayoutRadius = maxAvailableRadius;
    }


    private void setupGlobalButtons() {
        int btnWidth = 70;
        int btnHeight = 20;
        int padding = 4;
        int x = this.width - btnWidth - padding;
        int y = padding;
        
        addGlobalBtn("Reset", x, y, "Action: Reset All");
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
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        for (int i = 0; i < players.size(); i++) {
            PlayerToken player = players.get(i);
            
            double angle = (-Math.PI / 2) + (i * (2 * Math.PI / players.size()));
            int x = (int) (centerX + currentLayoutRadius * Math.cos(angle));
            int y = (int) (centerY + currentLayoutRadius * Math.sin(angle));

            player.renderX = x;
            player.renderY = y;

            int drawX = x - (currentTokenSize / 2);
            int drawY = y - (currentTokenSize / 2);

            boolean isHovered = false;
            if (selectedPlayer == null) {
                double dist = Math.sqrt(Math.pow(mouseX - x, 2) + Math.pow(mouseY - y, 2));
                if (dist <= currentTokenSize / 2.0) isHovered = true;
            }

            context.drawTexture(
                    RenderPipelines.GUI_TEXTURED, 
                    TOKEN_TEXTURE,
                    drawX, drawY,
                    0f, 0f,
                    currentTokenSize, currentTokenSize,
                    currentTokenSize, currentTokenSize
            );

            if (currentTokenSize > 20) {
                Text name = Text.of(player.name);
                int nameW = this.textRenderer.getWidth(name);
                // Adjust text position based on size
                context.drawTextWithShadow(this.textRenderer, name, x - (nameW / 2), drawY + currentTokenSize + 2, 0xFFFFAA00);
            }
        }
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
package online.refract.client.gui;

import java.util.ArrayList;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class TokenRenderer {

    private static final ResourceLocation TOKEN_TEXTURE = ResourceLocation.fromNamespaceAndPath("sttk", "textures/gui/token-greyscale.png");

    private static final int MAX_TOKEN_SIZE = 100; 
    private static final int TOKEN_MARGIN = 1;    
    private static final int SCREEN_PADDING = 2; 

    public int currentTokenSize;
    private int currentLayoutRadius;



    public void calculateTokenLayout(int playerCount, int width, int height) {
        if (playerCount == 0) return;

        double maxScreenRadius = (Math.min(width, height) / 2.0) - SCREEN_PADDING;

        if (playerCount == 1) {
            currentTokenSize = MAX_TOKEN_SIZE;
            currentLayoutRadius = 0;
            return;
        }

        double sinN = Math.sin(Math.PI / playerCount);
        double maxFeasibleSize = (2 * maxScreenRadius * sinN - TOKEN_MARGIN) / (1 + sinN);

        int calculatedSize = (int) Math.min(MAX_TOKEN_SIZE, maxFeasibleSize);
        currentTokenSize = Math.max(10, calculatedSize); 
        currentLayoutRadius = (int) (maxScreenRadius - (currentTokenSize / 2.0));
        
        if (currentLayoutRadius < 0) currentLayoutRadius = 0;
    }



    public void drawTokenCircle(GuiGraphics context, Font textRenderer, ArrayList<PlayerToken> players, int virtualWidth, int virtualHeight) {
        if (players.isEmpty()) return;

        int centerX = virtualWidth / 2;
        int centerY = virtualHeight / 2;
        double startAngle = -Math.PI / 2;

        for (int i = 0; i < players.size(); i++) {
            PlayerToken player = players.get(i);
            double angle = startAngle + (i * (2 * Math.PI / players.size()));
            int x = (int) (centerX + currentLayoutRadius * Math.cos(angle));
            int y = (int) (centerY + currentLayoutRadius * Math.sin(angle));

            player.renderX = x;
            player.renderY = y;

            int drawX = x - (currentTokenSize / 2);
            int drawY = y - (currentTokenSize / 2);

            drawToken(context, player, drawX, drawY, currentTokenSize);
        }

        for (int i = 0; i < players.size(); i++) {
            PlayerToken player = players.get(i);
            int drawX = player.renderX - (currentTokenSize / 2);
            int drawY = player.renderY - (currentTokenSize / 2);
            drawNames(context, textRenderer, player, drawX, drawY, currentTokenSize);
        }
    }



    public void drawToken(GuiGraphics context, PlayerToken player, int x, int y, int size) {
        context.blit(
                RenderPipelines.GUI_TEXTURED, 
                TOKEN_TEXTURE,
                x, y,
                0f, 0f,
                size, size,
                size, size,
                0xAAFFFFFF
        );
    }



    public void drawNames(GuiGraphics context, Font textRenderer, PlayerToken player, int x, int y, int size){
        Component name = Component.nullToEmpty(player.name);
        int nameW = textRenderer.width(name);
        int textX = x + (size / 2) - (nameW / 2);
        int textY = y + 4;
        context.drawString(textRenderer, name, textX , textY, 0xFFFFAA00);
    }



    @Nullable
    public PlayerToken handleTokenClick(ArrayList<PlayerToken> players, double mouseX, double mouseY, int button){
        if (button == 0) {
            for (PlayerToken player : players) {
                double distFromToken = Math.sqrt(Math.pow(mouseX - player.renderX, 2) + Math.pow(mouseY - player.renderY, 2));
                if (distFromToken <= (currentTokenSize / 2.0)) {
                    return player;
                }
            }
        }
        return null;
    }


}

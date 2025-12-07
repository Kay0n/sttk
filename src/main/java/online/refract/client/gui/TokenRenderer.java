package online.refract.client.gui;

import java.util.ArrayList;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class TokenRenderer {

    private static final Identifier TOKEN_TEXTURE = Identifier.of("sttk", "textures/gui/token-greyscale.png");

    private static final int MAX_TOKEN_SIZE = 100; 
    private static final int TOKEN_MARGIN = 1;    
    private static final int SCREEN_PADDING = 2; 

    public int currentTokenSize;
    private int currentLayoutRadius;



    public void calculateTokenLayout(int playerCount, int virtualWidth, int virtualHeight) {
        if (playerCount == 0) return;

        double maxScreenRadius = (Math.min(virtualWidth, virtualHeight) / 2.0) - SCREEN_PADDING;

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



    public void drawTokenCircle(DrawContext context, TextRenderer textRenderer, ArrayList<PlayerToken> players, int mouseX, int mouseY, int virtualWidth, int virtualHeight) {
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

            drawToken(context, player, drawX, drawY, currentTokenSize, mouseX, mouseY);
        }

        for (int i = 0; i < players.size(); i++) {
            PlayerToken player = players.get(i);
            int drawX = player.renderX - (currentTokenSize / 2);
            int drawY = player.renderY - (currentTokenSize / 2);
            drawNames(context, textRenderer, player, drawX, drawY, currentTokenSize, mouseX, mouseY);
        }
    }



    public void drawToken(DrawContext context, PlayerToken player, int x, int y, int size, int mouseX, int mouseY) {
        context.drawTexture(
                RenderPipelines.GUI_TEXTURED, 
                TOKEN_TEXTURE,
                x, y,
                0f, 0f,
                currentTokenSize, currentTokenSize,
                currentTokenSize, currentTokenSize,
                0xAAFFFFFF
        );
    }



    public void drawNames(DrawContext context, TextRenderer textRenderer, PlayerToken player, int x, int y, int size, int mouseX, int mouseY){
        Text name = Text.of(player.name);
        int nameW = textRenderer.getWidth(name);
        int textX = x + (currentTokenSize / 2) - (nameW / 2);
        int textY = y + 4;
        context.drawTextWithShadow(textRenderer, name, textX , textY, 0xFFFFAA00);
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

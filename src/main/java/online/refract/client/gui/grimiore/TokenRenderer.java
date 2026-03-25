package online.refract.client.gui.grimiore;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import online.refract.game.state.ClocktowerPlayer;

public class TokenRenderer {
    private static final ResourceLocation TOKEN_TEXTURE = ResourceLocation.fromNamespaceAndPath("sttk", "textures/gui/token-greyscale.png");
    private static final int MAX_TOKEN_SIZE = 100;
    private static final int MIN_TOKEN_SIZE = 10;
    private static final int TOKEN_MARGIN   = 1;
    private static final int SCREEN_PADDING = 2;
    private static final int LABEL_COLOR    = 0xFFFFAA00;

    private int tokenSize;
    private int layoutRadius;

    private record PositionedToken(ClocktowerPlayer player, int x, int y) {}



    private void calculateLayout(int n, int width, int height) {
        if (n == 0) return;
        double maxR = (Math.min(width, height) / 2.0) - SCREEN_PADDING;
        if (n == 1) { tokenSize = MAX_TOKEN_SIZE; layoutRadius = 0; return; }
        double sin = Math.sin(Math.PI / n);
        tokenSize    = Math.clamp((int)((2 * maxR * sin - TOKEN_MARGIN) / (1 + sin)), MIN_TOKEN_SIZE, MAX_TOKEN_SIZE);
        layoutRadius = Math.max(0, (int)(maxR - tokenSize / 2.0));
    }



    private List<PositionedToken> layout(List<ClocktowerPlayer> players, int width, int height) {
        calculateLayout(players.size(), width, height);
        int cx = width / 2, cy = height / 2;
        double step = 2 * Math.PI / players.size();
        List<PositionedToken> result = new ArrayList<>(players.size());
        for (int i = 0; i < players.size(); i++) {
            double a = -Math.PI / 2 + i * step;
            int x = (int)(cx + layoutRadius * Math.cos(a));
            int y = (int)(cy + layoutRadius * Math.sin(a));
            result.add(new PositionedToken(players.get(i), x, y));
        }
        return result;
    }
    


    public void render(GuiGraphics gfx, Font font, List<ClocktowerPlayer> players, int width, int height) {
        if (players.isEmpty()) return;
        List<PositionedToken> positioned = layout(players, width, height);

        for (PositionedToken pt : positioned)
            gfx.blit(RenderPipelines.GUI_TEXTURED, TOKEN_TEXTURE, pt.x() - tokenSize / 2, pt.y() - tokenSize / 2, 0f, 0f, tokenSize, tokenSize, tokenSize, tokenSize, 0xAAFFFFFF);
        for (PositionedToken pt : positioned)
            gfx.drawString(font, pt.player().name, pt.x() - font.width(pt.player().name) / 2, pt.y() - (tokenSize / 2) + 4, LABEL_COLOR);
    }



 @Nullable
  public ClocktowerPlayer hitTest(List<ClocktowerPlayer> players, double mouseX, double mouseY, int width, int height) {
        if (players.isEmpty()) return null;
        List<PositionedToken> positioned = layout(players, width, height);
        double r = tokenSize / 2.0;
        for (PositionedToken pt : positioned) {
            double dx = mouseX - pt.x(), dy = mouseY - pt.y();
            if (dx * dx + dy * dy <= r * r) return pt.player();
        }
        return null;
    }
}
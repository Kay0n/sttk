package online.refract.client.gui.screens.grimiore;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import online.refract.game.state.ClocktowerPlayer;

public class TokenRenderer {
    private static final ResourceLocation TOKEN_TEXTURE  = ResourceLocation.fromNamespaceAndPath("sttk", "textures/gui/token-greyscale.png");
    private static final ResourceLocation SHROUD_TEXTURE = ResourceLocation.fromNamespaceAndPath("sttk", "textures/gui/shroud.png");
    private static final ResourceLocation VOTE_TEXTURE   = ResourceLocation.fromNamespaceAndPath("sttk", "textures/gui/hand.png");

    private static final int MAX_TOKEN_SIZE = 100;
    private static final int MIN_TOKEN_SIZE = 10;
    private static final int TOKEN_MARGIN = 1;
    private static final int SCREEN_PADDING  = 2;
    private static final int LABEL_COLOR = 0xFFFFAA00;
    private static final int PILL_PAD_X = 4;
    private static final int PILL_PAD_Y = 2;
    private static final float SHROUD_WIDTH_FRACTION  = 0.25f;
    private static final float SHROUD_ASPECT = 256f / 384f; 
    private static final float VOTE_SCALE = 0.8f;
    private static final float VOTE_ASPECT = 32f / 32f;

    private int tokenSize;
    private int layoutRadius;

    private record PositionedToken(ClocktowerPlayer player, int x, int y) {}
    private record Rect(int x, int y, int w, int h) {}



    public void render(GuiGraphics gfx, Font font, List<ClocktowerPlayer> players, int width, int height) {
        if (players.isEmpty()) return;
        List<PositionedToken> positioned = calculateLayout(players, width, height);

        for (PositionedToken token : positioned) {
            int x = token.x() - tokenSize / 2;
            int y = token.y() - tokenSize / 2;
            gfx.blit(RenderPipelines.GUI_TEXTURED, TOKEN_TEXTURE, x, y, 0f, 0f, tokenSize, tokenSize, tokenSize, tokenSize, 0xAAFFFFFF);
        }

        for (PositionedToken token : positioned) {
            if (!token.player().isDead()) continue;
            Rect shroudRect = shroudRect(token);
            drawTexturedRect(gfx, SHROUD_TEXTURE, shroudRect, 0xFFFFFFFF);

            if (token.player().hasUsedGhostVote()) continue;
            drawTexturedRect(gfx, VOTE_TEXTURE, voteRect(shroudRect), 0xFFFFFFFF);
        }

        for (PositionedToken token : positioned) {
            drawTextWithPill(gfx, font, token);
        }
    }


    private Rect shroudRect(PositionedToken token) {
        int w = (int)(tokenSize * SHROUD_WIDTH_FRACTION);
        int h = (int)(w / SHROUD_ASPECT);          
        int x = token.x() - w / 2;
        int y = token.y() - tokenSize / 2;
        return new Rect(x, y, w, h);
    }

    private Rect voteRect(Rect shroud) {
        int w = (int)(shroud.w() * VOTE_SCALE);
        int h = (int)(w / VOTE_ASPECT);             
        int x = shroud.x() + (shroud.w() - w) / 2;
        int y = shroud.y() + (shroud.h() - h) / 2;
        return new Rect(x, y, w, h);
    }


    private static void drawTexturedRect(GuiGraphics gfx, ResourceLocation tex, Rect r, int tint) {
        gfx.blit(RenderPipelines.GUI_TEXTURED, tex, r.x(), r.y(), 0f, 0f, r.w(), r.h(), r.w(), r.h(), tint);
    }

    public void drawTextWithPill(GuiGraphics gfx, Font font, PositionedToken token) {
        String name  = token.player().name();
        int textW    = font.width(name);
        int textH    = font.lineHeight;
        int textX    = token.x() - textW / 2;
        int textY    = token.y() + tokenSize / 2 - textH;

        int pillX = textX - PILL_PAD_X;
        int pillY = textY - PILL_PAD_Y;
        drawTextBackgroundPill(gfx, pillX, pillY, textW + 2 * PILL_PAD_X, textH + 2 * PILL_PAD_Y, 0x66FFFFFF);
        gfx.drawString(font, name, textX, textY, LABEL_COLOR);
    }

    public static void drawTextBackgroundPill(GuiGraphics gfx, int x, int y, int width, int height, int color) {
        int alpha = (color >> 24) & 0xFF;
        int red   = (color >> 16) & 0xFF;
        int green = (color >>  8) & 0xFF;
        int blue  =  color        & 0xFF;

        float radius      = height / 2f;
        float centerY     = radius;
        float radiusSq    = radius * radius;
        float rightRadius = width - radius;

        for (int row = 0; row < height; row++) {
            float distFromCenter = (row + 0.5f) - centerY;
            float halfChord  = (float) Math.sqrt(Math.max(0, radiusSq - distFromCenter * distFromCenter));
            float leftEdge   = radius - halfChord;
            float rightEdge  = rightRadius + halfChord;
            int   fullLeft   = (int) Math.ceil(leftEdge);
            int   fullRight  = (int) Math.floor(rightEdge);

            gfx.fill(x + fullLeft, y + row, x + fullRight, y + row + 1, color);

            int leftAlpha = (int)(alpha * (1f - (leftEdge - (fullLeft - 1))));
            if (leftAlpha > 0 && leftAlpha < 255)
                gfx.fill(x + fullLeft - 1, y + row, x + fullLeft, y + row + 1,
                        (leftAlpha << 24) | (red << 16) | (green << 8) | blue);

            int rightAlpha = (int)(alpha * (rightEdge - fullRight));
            if (rightAlpha > 0 && rightAlpha < 255)
                gfx.fill(x + fullRight, y + row, x + fullRight + 1, y + row + 1,
                        (rightAlpha << 24) | (red << 16) | (green << 8) | blue);
        }
    }

    @Nullable
    public ClocktowerPlayer wasTokenClicked(List<ClocktowerPlayer> players, double mouseX, double mouseY, int width, int height) {
        if (players.isEmpty()) return null;
        List<PositionedToken> positioned = calculateLayout(players, width, height);
        double r = tokenSize / 2.0;
        for (PositionedToken pt : positioned) {
            double dx = mouseX - pt.x(), dy = mouseY - pt.y();
            if (dx * dx + dy * dy <= r * r) return pt.player();
        }
        return null;
    }

    private void calculateCircle(int n, int width, int height) {
        if (n == 0) return;
        double maxR = (Math.min(width, height) / 2.0) - SCREEN_PADDING;
        if (n == 1) { tokenSize = MAX_TOKEN_SIZE; layoutRadius = 0; return; }
        double sin = Math.sin(Math.PI / n);
        tokenSize    = Math.clamp((int)((2 * maxR * sin - TOKEN_MARGIN) / (1 + sin)), MIN_TOKEN_SIZE, MAX_TOKEN_SIZE);
        layoutRadius = Math.max(0, (int)(maxR - tokenSize / 2.0));
    }

    private List<PositionedToken> calculateLayout(List<ClocktowerPlayer> players, int width, int height) {
        calculateCircle(players.size(), width, height);
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
}
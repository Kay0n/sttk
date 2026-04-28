package online.refract.client.gui.screens.grimiore;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;

import online.refract.client.ClientAssetCache;
import online.refract.game.state.ClocktowerPlayer;

public class TokenRenderer {

    private static final ResourceLocation SHROUD_TEXTURE = ResourceLocation.fromNamespaceAndPath("sttk", "textures/gui/shroud.png");
    private static final ResourceLocation VOTE_TEXTURE   = ResourceLocation.fromNamespaceAndPath("sttk", "textures/gui/hand.png");

    private static final int   TOKEN_SIZE_MAX    = 80;
    private static final int   TOKEN_SIZE_MIN    = 10;
    private static final int   TOKEN_MARGIN      = 1;
    private static final int   SCREEN_PADDING    = 2;
    private static final float ROLE_ICON_SCALE   = 0.60f;

    private static final int COLOR_DISK_FILL   = 0xFF1E1E2E;
    private static final int COLOR_DISK_BORDER = 0xFF44445A;

    private static final int COLOR_NAME_LABEL        = 0xFFFFFFFF;
    private static final int COLOR_NAME_LABEL_UNLINKED = 0xFFAA4444;
    private static final int COLOR_NAME_BACKGROUND   = 0xCC000000;
    private static final int COLOR_NAME_BORDER       = 0xFF6A6A8A;
    private static final int NAME_STRIP_PADDING      = 2;

    private static final float SHROUD_WIDTH_FRACTION = 0.25f;
    private static final float SHROUD_ASPECT         = 256f / 384f;

    private static final float VOTE_SCALE  = 0.8f;
    private static final float VOTE_ASPECT = 32f / 32f;

    private record TokenLayout(int tokenSize, int layoutRadius) {}
    private record PositionedToken(ClocktowerPlayer player, int x, int y) {}


    public void render(GuiGraphics gfx, Font font, List<ClocktowerPlayer> players, int width, int height, ClientAssetCache assetCache) {
        if (players.isEmpty()) return;

        TokenLayout layout = calculateLayout(players.size(), width, height);
        List<PositionedToken> positionedTokens = positionPlayers(players, layout, width, height);

        renderTokenDisks(gfx, positionedTokens, layout);
        renderRoleIcons(gfx, positionedTokens, layout, assetCache);
        renderDeathOverlays(gfx, positionedTokens, layout);
        renderNameStrips(gfx, font, positionedTokens, layout);
    }


    private void renderTokenDisks(GuiGraphics gfx, List<PositionedToken> positioned, TokenLayout layout) {
        for (PositionedToken token : positioned) {
            int x = token.x() - layout.tokenSize() / 2;
            int y = token.y() - layout.tokenSize() / 2;
            drawTokenDisk(gfx, x, y, layout.tokenSize());
        }
    }


    private void renderRoleIcons(GuiGraphics gfx, List<PositionedToken> positioned, TokenLayout layout, ClientAssetCache assetCache) {
        int iconSize = (int)(layout.tokenSize() * ROLE_ICON_SCALE);
        for (PositionedToken token : positioned) {
            ResourceLocation iconTex = assetCache.getTexture(token.player().alignedIconUrl());
            if (iconTex == null) continue;
            int x = token.x() - iconSize / 2;
            int y = token.y() - iconSize / 2;
            gfx.blit(RenderPipelines.GUI_TEXTURED, iconTex, x, y, 0f, 0f, iconSize, iconSize, iconSize, iconSize, 0xFFFFFFFF);
        }
    }


    private void renderDeathOverlays(GuiGraphics gfx, List<PositionedToken> positioned, TokenLayout layout) {
        for (PositionedToken token : positioned) {
            if (!token.player().isDead()) continue;

            int shroudW = (int)(layout.tokenSize() * SHROUD_WIDTH_FRACTION);
            int shroudH = (int)(shroudW / SHROUD_ASPECT);
            int shroudX = token.x() - shroudW / 2;
            int shroudY = token.y() - layout.tokenSize() / 2;
            drawTexturedRect(gfx, SHROUD_TEXTURE, shroudX, shroudY, shroudW, shroudH);

            if (!token.player().hasUsedGhostVote()) {
                int voteW = (int)(shroudW * VOTE_SCALE);
                int voteH = (int)(voteW / VOTE_ASPECT);
                int voteX = shroudX + (shroudW - voteW) / 2;
                int voteY = shroudY + (shroudH - voteH) / 2;
                drawTexturedRect(gfx, VOTE_TEXTURE, voteX, voteY, voteW, voteH);
            }
        }
    }


    private void renderNameStrips(GuiGraphics gfx, Font font, List<PositionedToken> positioned, TokenLayout layout) {
        for (PositionedToken token : positioned) {
            drawNameStrip(gfx, font, token, layout);
        }
    }


    private static void drawTokenDisk(GuiGraphics gfx, int x, int y, int size) {
        float cx = x + size / 2f;
        float cy = y + size / 2f;
        float r  = size / 2f;

        for (int row = 0; row < size; row++) {
            float rowCenterY = y + row + 0.5f;
            float dy         = rowCenterY - cy;
            float halfChord  = (float) Math.sqrt(Math.max(0, r * r - dy * dy));

            int left  = (int) Math.ceil(cx - halfChord);
            int right = (int) Math.floor(cx + halfChord);

            gfx.fill(left, y + row, right, y + row + 1, COLOR_DISK_FILL);

            // Side edges
            gfx.fill(left,      y + row, left + 1,  y + row + 1, COLOR_DISK_BORDER);
            gfx.fill(right - 1, y + row, right,     y + row + 1, COLOR_DISK_BORDER);

            // Top and bottom caps: if the row above or below falls outside the circle, this row is a cap
            boolean isTopCap    = r * r - (rowCenterY - 1 - cy) * (rowCenterY - 1 - cy) < 0;
            boolean isBottomCap = r * r - (rowCenterY + 1 - cy) * (rowCenterY + 1 - cy) < 0;

            if (isTopCap || isBottomCap) {
                gfx.fill(left, y + row, right, y + row + 1, COLOR_DISK_BORDER);
            }
        }
    }


    private static void drawNameStrip(GuiGraphics gfx, Font font, PositionedToken token, TokenLayout layout) {
        String name  = token.player().name();
        int    textW = font.width(name);
        int    textH = font.lineHeight;
        int    textX = token.x() - textW / 2;
        int    textY = token.y() + layout.tokenSize() / 2 - textH;

        int p = NAME_STRIP_PADDING;
        int labelColor = token.player().linkedMinecraftUsername() != null
                ? COLOR_NAME_LABEL
                : COLOR_NAME_LABEL_UNLINKED;

        gfx.fill(textX - p, textY - p, textX + textW + p, textY + textH + p, COLOR_NAME_BACKGROUND);
        gfx.renderOutline(textX - p, textY - p, textW + p * 2, textH + p * 2, COLOR_NAME_BORDER);
        gfx.drawString(font, name, textX, textY, labelColor);
    }


    private static void drawTexturedRect(GuiGraphics gfx, ResourceLocation tex, int x, int y, int w, int h) {
        gfx.blit(RenderPipelines.GUI_TEXTURED, tex, x, y, 0f, 0f, w, h, w, h, 0xFFFFFFFF);
    }


    private static TokenLayout calculateLayout(int playerCount, int width, int height) {
        if (playerCount == 0) return new TokenLayout(TOKEN_SIZE_MIN, 0);
        if (playerCount == 1) return new TokenLayout(TOKEN_SIZE_MAX, 0);

        double maxRadius = (Math.min(width, height) / 2.0) - SCREEN_PADDING;
        double sin        = Math.sin(Math.PI / playerCount);
        int    tokenSize  = Math.clamp((int)((2 * maxRadius * sin - TOKEN_MARGIN) / (1 + sin)), TOKEN_SIZE_MIN, TOKEN_SIZE_MAX);
        int    ringRadius = Math.max(0, (int)(maxRadius - tokenSize / 2.0));

        return new TokenLayout(tokenSize, ringRadius);
    }


    private static List<PositionedToken> positionPlayers(List<ClocktowerPlayer> players, TokenLayout layout, int width, int height) {
        int    cx     = width / 2;
        int    cy     = height / 2;
        int    n      = players.size();
        double step   = 2 * Math.PI / n;

        List<PositionedToken> result = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            double angle = -Math.PI / 2 + i * step;
            int    x     = (int)(cx + layout.layoutRadius() * Math.cos(angle));
            int    y     = (int)(cy + layout.layoutRadius() * Math.sin(angle));
            result.add(new PositionedToken(players.get(i), x, y));
        }
        return result;
    }


    @Nullable
    public ClocktowerPlayer wasTokenClicked(List<ClocktowerPlayer> players, double mouseX, double mouseY, int width, int height) {
        if (players.isEmpty()) return null;

        TokenLayout layout = calculateLayout(players.size(), width, height);
        List<PositionedToken> positioned = positionPlayers(players, layout, width, height);

        double r = layout.tokenSize() / 2.0;
        for (PositionedToken pt : positioned) {
            double dx = mouseX - pt.x();
            double dy = mouseY - pt.y();
            if (dx * dx + dy * dy <= r * r) return pt.player();
        }
        return null;
    }

}
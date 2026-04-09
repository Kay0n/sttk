
package online.refract.client.gui.screens.script;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import online.refract.client.ClientAssetCache;
import online.refract.game.state.ClocktowerRole;
import online.refract.game.state.Enums.RoleType;


public class CardRowEntry extends RoleListWidget.Entry {

    private static final int CARD_MARGIN_H        = 2;
    public  static final int CARD_MARGIN_V        = 2;

    private static final int CARD_PADDING_H    = 3;
    private static final int CARD_PADDING_V    = 3;
    private static final int NAME_TO_ABILITY_GAP = 3;
    private static final int ABILITY_LINE_SPACING = 1;
    private static final int ICON_TO_TEXT_GAP  = 4;

    private static final int ICON_SIZE         = 32;
    private static final int ICON_RENDER_SIZE  = 44; 

    public  static final float ABILITY_SCALE   = 0.60f;
    private static final int MAX_ABILITY_LINES = 8;

    private static final int COLOR_CARD_BG     = 0xFF1E1E2E;
    private static final int COLOR_CARD_BORDER = 0xFF44445A;
    private static final int COLOR_ABILITY     = 0xFFAAAAAA;

    private final ClocktowerRole left;
    @Nullable private final ClocktowerRole right;
    private final ClientAssetCache assetCache;
    private final int rowHeight;

    public CardRowEntry(ClocktowerRole left, @Nullable ClocktowerRole right, int rowHeight, ClientAssetCache assetCache) {
        this.left       = left;
        this.right      = right;
        this.rowHeight  = rowHeight;
        this.assetCache = assetCache;
    }


    @Override
    public void render(GuiGraphics g, int index, int top, int x, int w, int h, int mX, int mY, boolean hovered, float pTick) {
        int cardWidth = cardWidth(w);
        renderCard(g, left, x, top, cardWidth);
        if (right != null) {
            renderCard(g, right, x + cardWidth + CARD_MARGIN_H, top, cardWidth);
        }
    }


    private void renderCard(GuiGraphics g, ClocktowerRole role, int x, int y, int cardWidth) {
        Font font = Minecraft.getInstance().font;

        int cardH = rowHeight - CARD_MARGIN_V;
        drawCard(g, x, y, cardWidth, cardH);

        int iconX = x + CARD_PADDING_H;
        int iconY = y + (cardH - ICON_SIZE) / 2;
        drawIcon(g, role, iconX, iconY);

        int textX     = iconX + ICON_SIZE + ICON_TO_TEXT_GAP;
        int textWidth = cardWidth - CARD_PADDING_H - ICON_SIZE - ICON_TO_TEXT_GAP - CARD_PADDING_H;
        int nameY = y + CARD_PADDING_V + 1;
        drawRoleName(g, font, role, textX, nameY);

        int abilityY = nameY + font.lineHeight + NAME_TO_ABILITY_GAP;
        drawAbilityText(g, font, role, textX, abilityY, textWidth);
    }


    private void drawCard(GuiGraphics g, int x, int y, int width, int height) {
        g.fill(x, y, x + width, y + height, COLOR_CARD_BG);
        g.renderOutline(x, y, width, height, COLOR_CARD_BORDER);
    }


    private void drawIcon(GuiGraphics g, ClocktowerRole role, int iconX, int iconY) {
        ResourceLocation icon = assetCache.getTexture(role.alignedIconUrl());

        int iconRenderOffset = (ICON_RENDER_SIZE - ICON_SIZE) / 2;

        g.blit(
            RenderPipelines.GUI_TEXTURED, icon,
            iconX - iconRenderOffset, iconY - iconRenderOffset,
             0, 0,
            ICON_RENDER_SIZE, ICON_RENDER_SIZE, ICON_RENDER_SIZE, ICON_RENDER_SIZE
        );
    }


    private void drawRoleName(GuiGraphics g, Font font, ClocktowerRole role, int textX, int nameY) {
        g.drawString(font, Component.literal(role.name()), textX, nameY, RoleType.textColor(role.type()));
    }


    private void drawAbilityText(GuiGraphics g, Font font, ClocktowerRole role, int textX, int abilityY, int textWidth) {
        int scaledLineHeight = scaledLineHeight(font);

        List<FormattedCharSequence> lines = font.split(Component.literal(role.abilityText()), (int) (textWidth / ABILITY_SCALE));

        g.pose().pushMatrix();
        g.pose().translate(textX, abilityY);
        g.pose().scale(ABILITY_SCALE, ABILITY_SCALE);

        int lineCount = Math.min(lines.size(), MAX_ABILITY_LINES);
        for (int i = 0; i < lineCount; i++) {
            int lineY = (int) ((i * scaledLineHeight) / ABILITY_SCALE);
            g.drawString(font, lines.get(i), 0, lineY, COLOR_ABILITY, false);
        }

        g.pose().popMatrix();
    }


    public static int computeCardHeight(ClocktowerRole role, int listWidth, Font font) {
        int textWidth = abilityTextWidth(listWidth);
        int abilityLines = Math.min(
            font.split(Component.literal(role.abilityText()),
            (int) (textWidth / ABILITY_SCALE)).size(),
            MAX_ABILITY_LINES
        );

        int textBlockHeight = font.lineHeight + NAME_TO_ABILITY_GAP + abilityLines * scaledLineHeight(font);

        int contentHeight = Math.max(ICON_SIZE, textBlockHeight);
        return contentHeight + CARD_PADDING_V * 2;
    }




    private static int cardWidth(int rowWidth) {
        return (rowWidth - CARD_MARGIN_H) / 2;
    }

    private static int abilityTextWidth(int listWidth) {
        return cardWidth(listWidth) - CARD_PADDING_H - ICON_SIZE - ICON_TO_TEXT_GAP - CARD_PADDING_H;
    }

    private static int scaledLineHeight(Font font) {
        return (int) Math.ceil(font.lineHeight * ABILITY_SCALE) + ABILITY_LINE_SPACING;
    }

    @Override
    public Component getNarration() {
        String rightName = right != null ? " and " + right.name() : "";
        return Component.literal(left.name() + rightName);
    }
}
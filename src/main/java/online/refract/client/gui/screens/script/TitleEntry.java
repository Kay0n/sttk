package online.refract.client.gui.screens.script;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import online.refract.game.state.Enums.RoleType;


public class TitleEntry extends RoleListWidget.Entry {

    private final float BANNER_SCALE = 0.25f;
    private final float TITLE_SCALE = 1.8f;
    private final int BOTTOM_MARGIN = 4;


    private final int height; 

    private final Component title;

    public TitleEntry(String editionName, int listWidth, int height) {
        this.title = Component.literal(editionName);
        this.height = height;
    }


    @Override
    public void render(GuiGraphics ctx, int i, int top, int left, int width, int h, int mx, int my, boolean ho, float p) {
        Font font = net.minecraft.client.Minecraft.getInstance().font;

        int bandHeight = (int)(height * BANNER_SCALE);
        int bandTop = top + height - bandHeight - BOTTOM_MARGIN;

        drawTitle( ctx,  bandHeight,  left,  top,  width,  font);
        drawLine(ctx, bandTop, left, width);
        drawTownsfolkBanner(ctx, bandHeight, bandTop, left, top, width, font);

    }


    private void drawLine(GuiGraphics ctx, int bandTop, int left, int width){
        int lineY = bandTop - 4;
        ctx.fill(left + 4, lineY, left + width - 4, lineY + 1, 0x44FFFFFF);
    }


    private void drawTitle(GuiGraphics ctx, int bandHeight, int left, int top, int width, Font font){
        int availableHeight = height - bandHeight - BOTTOM_MARGIN;
        float cx = left + width / 2f;
        float cy = top + (availableHeight / 2f);
        int textWidth = font.width(title);

        ctx.pose().pushMatrix();
        ctx.pose().translate(cx, cy);
        ctx.pose().scale(TITLE_SCALE, TITLE_SCALE);
        ctx.drawString(font, title, -(textWidth / 2), -(font.lineHeight / 2), 0xFFFFFFFF);
        ctx.pose().popMatrix();
    }


    private void drawTownsfolkBanner(GuiGraphics ctx, int bandHeight, int bandTop, int left, int top, int width, Font font){
        ctx.fill(left, bandTop, left + width, bandTop + bandHeight, RoleType.bgColor(RoleType.TOWNSFOLK));

        Component roleLabel = Component.literal("Townsfolk");
        int roleTextX = left + (width - font.width(roleLabel)) / 2;
        int roleTextY = bandTop + (bandHeight - font.lineHeight) / 2 + 1;

        ctx.drawString(font, roleLabel, roleTextX, roleTextY, RoleType.textColor(RoleType.TOWNSFOLK));
    }


    @Override
    public Component getNarration() {
        return title;
    }
}
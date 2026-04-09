package online.refract.client.gui.screens.script;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import online.refract.game.state.Enums.RoleType;


public class RoleTypeEntry extends RoleListWidget.Entry {

    public final int height;
    private final Component label;
    private final RoleType type;


    public RoleTypeEntry(RoleType type, int listWidth,int entryHeight) {
        this.type = type;
        this.height = entryHeight;

        String rawText = type.getSerializedName();
        this.label = Component.literal(
            rawText.substring(0, 1).toUpperCase() + rawText.substring(1).toLowerCase()
        );
    }


    @Override
    public void render(GuiGraphics ctx, int i, int y, int x, int width, int height, int mx, int my, boolean ho, float p) {

        Font font = Minecraft.getInstance().font;
        float scale = 0.33f;

        int bandHeight = (int)(height * scale);
        int bandTop = y + height - bandHeight; 

        if (bandHeight > 0) {
            ctx.fill(x, bandTop, x + width, bandTop + bandHeight, RoleType.bgColor(type));

            int textX = x + (width - font.width(label)) / 2;
            int textY = bandTop + (bandHeight - font.lineHeight) / 2 + 1; // +1 because minecraft text is vertically off center

            ctx.drawString(font, label, textX, textY, RoleType.textColor(type));
        }
    }

    
    @Override
    public Component getNarration() {
        return label;
    }
}

package online.refract.client.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class StaticTextWidget extends AbstractWidget {
    private final Font font;
    private final int color;
    private final float scale;

    public StaticTextWidget(Font font, Component text, int color , float scale) {
        super(0, 0, 0, (int)(9 * scale), text);
        this.font = font;
        this.color = color;
        this.scale = scale;
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        var pose = context.pose();
        pose.pushMatrix();
        pose.translate(getX(), getY());
        pose.scale(scale, scale);

        float centerX = (getWidth() / scale) / 2f;
        context.drawCenteredString(font, getMessage(), (int) centerX, 0, color);


        pose.popMatrix();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }


}

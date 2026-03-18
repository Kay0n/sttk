package online.refract.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;


public class GuiScale {
    
    private static final float BASE_RESOLUTION_HEIGHT = 540f * 0.64f;
    public record MouseCoords(int x, int y) {}


    public static float getDynamicScale() {
        int physHeight = Minecraft.getInstance().getWindow().getHeight();
        if (physHeight == 0) return 1.0f;
        return physHeight / BASE_RESOLUTION_HEIGHT;
    }


    public static int getUnscaledWidth() {
        return (int) (Minecraft.getInstance().getWindow().getWidth() / getDynamicScale());
    }


    public static int getUnscaledHeight() {
        return (int) (Minecraft.getInstance().getWindow().getHeight() / getDynamicScale());
    }


    public static MouseCoords getUnscaledMouseCoords(double mouseX, double mouseY) {
        double guiScale = Minecraft.getInstance().getWindow().getGuiScale();
        float dynamicScale = getDynamicScale();
        int virtualX = (int)(mouseX * (guiScale / dynamicScale));
        int virtualY = (int)(mouseY * (guiScale / dynamicScale));
        return new MouseCoords(virtualX, virtualY);
    }


    public static void disableGuiScale(GuiGraphics context) {
        double guiScale = Minecraft.getInstance().getWindow().getGuiScale();
        float dynamicScale = getDynamicScale();
        float matrixScale = (float) (dynamicScale / guiScale);
        context.pose().pushMatrix();
        context.pose().scale(matrixScale, matrixScale);
    }


    public static void enableGuiScale(GuiGraphics context) {
        context.pose().popMatrix();
    }
    
}

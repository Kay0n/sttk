package online.refract.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;


public class GuiScale {
    
    private static final float BASE_RESOLUTION_HEIGHT = 540f * 0.64f;
    public record MouseCoords(int x, int y) {}


    public static float getDynamicScale() {
        int physHeight = MinecraftClient.getInstance().getWindow().getFramebufferHeight();
        if (physHeight == 0) return 1.0f;
        return physHeight / BASE_RESOLUTION_HEIGHT;
    }


    public static int getUnscaledWidth() {
        return (int) (MinecraftClient.getInstance().getWindow().getFramebufferWidth() / getDynamicScale());
    }


    public static int getUnscaledHeight() {
        return (int) (MinecraftClient.getInstance().getWindow().getFramebufferHeight() / getDynamicScale());
    }


    public static MouseCoords getUnscaledMouseCoords(double mouseX, double mouseY) {
        double guiScale = MinecraftClient.getInstance().getWindow().getScaleFactor();
        float dynamicScale = getDynamicScale();
        int virtualX = (int)(mouseX * (guiScale / dynamicScale));
        int virtualY = (int)(mouseY * (guiScale / dynamicScale));
        return new MouseCoords(virtualX, virtualY);
    }


    public static void disableGuiScale(DrawContext context) {
        double guiScale = MinecraftClient.getInstance().getWindow().getScaleFactor();
        float dynamicScale = getDynamicScale();
        float matrixScale = (float) (dynamicScale / guiScale);
        context.getMatrices().pushMatrix();
        context.getMatrices().scale(matrixScale, matrixScale);
    }


    public static void enableGuiScale(DrawContext context) {
        context.getMatrices().popMatrix();
    }
    
}

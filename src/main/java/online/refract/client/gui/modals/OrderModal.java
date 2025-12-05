package online.refract.client.gui.modals;

import java.util.ArrayList;
import java.util.Collections;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import online.refract.client.gui.PlayerToken;

public class OrderModal {

    // layout constants
    private static final int BASE_WIDTH = 160;
    private static final int ITEM_HEIGHT = 22;
    private static final int HEADER_HEIGHT = 25;
    private static final int FOOTER_HEIGHT = 35;
    private static final int PADDING = 10;

    // scale limits
    private static final float MAX_SCALE = 1.5f;
    private static final float MIN_SCALE = 0.5f;

    // colors
    private static final int BG_DIM = 0x80000000;
    private static final int MODAL_BG = 0xFF202020;
    private static final int MODAL_BORDER = 0xFFFFFFFF;
    private static final int HOLE_COLOR = 0xFF101010;
    private static final int BTN_COLOR = 0xFF404040;
    private static final int BTN_HOVER = 0xFF606060;

    // state
    private boolean open = false;
    private ArrayList<PlayerToken> originalList;
    private ArrayList<PlayerToken> workingList;

    private int draggingIndex = -1;
    private float currentScale = 1.0f;

    private int dragOffsetX = 0;
    private int dragOffsetY = 0;

    private int modalX, modalY, modalW, modalH;
    private int saveBtnY;



    public void openModal(ArrayList<PlayerToken> tokens) {
        this.originalList = tokens;
        this.workingList = new ArrayList<>(tokens); 
        this.open = true;
        this.draggingIndex = -1;
    }



    public void closeModal() {
        this.open = false;
        this.draggingIndex = -1;
        this.workingList = null;
        this.originalList = null;
    }



    public boolean isOpen() { return open; }



    public void render(DrawContext ctx, TextRenderer tr, int mouseX, int mouseY, float delta, int screenW, int screenH) {
        if (!open || workingList == null) return;

        calculateLayout(screenW, screenH);
        applyDragLiveSwap(mouseX, mouseY);

        drawDimBackground(ctx, screenW, screenH);

        ctx.getMatrices().pushMatrix();
        ctx.getMatrices().translate(modalX, modalY);
        ctx.getMatrices().scale(currentScale, currentScale);

        drawModalFrame(ctx);
        drawTitle(ctx, tr);
        drawPlayerList(ctx, tr);
        drawSaveButton(ctx, tr, mouseX, mouseY);

        ctx.getMatrices().popMatrix();

        drawFloatingDraggedItem(ctx, tr, mouseX, mouseY);
    }



    private void calculateLayout(int screenW, int screenH) {
        int listH = workingList.size() * ITEM_HEIGHT;
        int totalH = PADDING + HEADER_HEIGHT + listH + FOOTER_HEIGHT + PADDING;

        float ratio = (float) (screenH - 40) / totalH;
        currentScale = MathHelper.clamp(ratio, MIN_SCALE, MAX_SCALE);

        modalW = (int) (BASE_WIDTH * currentScale);
        modalH = (int) (totalH * currentScale);
        modalX = (screenW - modalW) / 2;
        modalY = (screenH - modalH) / 2;

        saveBtnY = totalH - FOOTER_HEIGHT;
    }



    private void applyDragLiveSwap(int mouseX, int mouseY) {
        if (draggingIndex == -1) return;

        int hoverIndex = getIndexAtPosition(mouseX, mouseY);
        if (hoverIndex != -1 && hoverIndex != draggingIndex && hoverIndex < workingList.size()) {
            Collections.swap(workingList, draggingIndex, hoverIndex);
            draggingIndex = hoverIndex;
        }
    }



    private void drawDimBackground(DrawContext ctx, int w, int h) {
        ctx.fillGradient(0, 0, w, h, BG_DIM, BG_DIM);
    }



    private void drawModalFrame(DrawContext ctx) {
        int totalH = (int) (modalH / currentScale);
        ctx.fill(0, 0, BASE_WIDTH, totalH, MODAL_BG);
        ctx.drawBorder(0, 0, BASE_WIDTH, totalH, MODAL_BORDER);
    }



    private void drawTitle(DrawContext ctx, TextRenderer tr) {
        ctx.drawCenteredTextWithShadow(tr, Text.of("Reorder Players"),
                BASE_WIDTH / 2, PADDING + 4, 0xFFFFFFFF);
    }



    private void drawPlayerList(DrawContext ctx, TextRenderer tr) {
        int startY = PADDING + HEADER_HEIGHT;

        for (int i = 0; i < workingList.size(); i++) {
            int y = startY + i * ITEM_HEIGHT;

            if (i == draggingIndex) {
                drawListBlank(ctx, y);
            } else {
                drawListItem(ctx, tr, workingList.get(i), y);
            }
        }
    }



    private void drawListBlank(DrawContext ctx, int y) {
        ctx.fill(5, y, BASE_WIDTH - 5, y + ITEM_HEIGHT, HOLE_COLOR);
        ctx.drawBorder(5, y, BASE_WIDTH - 10, ITEM_HEIGHT, 0xFF555555);
    }



    private void drawListItem(DrawContext ctx, TextRenderer tr, PlayerToken token, int y) {
        ctx.drawTextWithShadow(tr, Text.of(token.name), 10, y + 7, 0xFFFFFFFF);
    }



    private void drawSaveButton(DrawContext ctx, TextRenderer tr, int mouseX, int mouseY) {
        boolean hovered = isHoveringSave(mouseX, mouseY);
        int color = hovered ? BTN_HOVER : BTN_COLOR;

        int btnX = 20;
        int btnY = saveBtnY;
        int btnW = BASE_WIDTH - 40;
        int btnH = 20;

        ctx.fill(btnX, btnY, btnX + btnW, btnY + btnH, color);
        ctx.drawBorder(btnX, btnY, btnW, btnH, 0xFFAAAAAA);
        ctx.drawCenteredTextWithShadow(tr, Text.of("Save Order"),
                BASE_WIDTH / 2, btnY + 6, 0xFFFFFFFF);
    }



    private void drawFloatingDraggedItem(DrawContext ctx, TextRenderer tr, int mouseX, int mouseY) {
        if (draggingIndex == -1) return;

        PlayerToken token = workingList.get(draggingIndex);
        int fx = mouseX + dragOffsetX;
        int fy = mouseY + dragOffsetY;
        int fw = (int) (BASE_WIDTH * currentScale);
        int fh = (int) (ITEM_HEIGHT * currentScale);

        ctx.fill(fx, fy, fx + fw, fy + fh, 0xFF404040);
        ctx.drawBorder(fx, fy, fw, fh, 0xFFFFFFFF);

        ctx.getMatrices().pushMatrix();
        ctx.getMatrices().translate(fx, fy);
        ctx.getMatrices().scale(currentScale, currentScale);
        ctx.drawTextWithShadow(tr, Text.of(token.name), 10, 7, 0xFFFFFFFF);
        ctx.getMatrices().popMatrix();
    }



    public boolean mouseClicked(int mx, int my, int button, int w, int h) {
        if (!open) return false;

        if (isHoveringSave(mx, my)) {
            saveAndClose();
            return true;
        }

        int index = getIndexAtPosition(mx, my);
        if (index != -1) {
            draggingIndex = index;
            
            float localItemY = (PADDING + HEADER_HEIGHT) + (index * ITEM_HEIGHT);
            
            int itemScreenX = modalX;
            int itemScreenY = modalY + (int)(localItemY * currentScale);

            dragOffsetX = itemScreenX - mx;
            dragOffsetY = itemScreenY - my;

            return true;
        }

        if (insideModal(mx, my)) return true;

        closeModal();
        return true;
    }



    public boolean mouseReleased(int mx, int my, int button, int w, int h) {
        if (!open) return false;
        if (draggingIndex != -1) {
            draggingIndex = -1;
            return true;
        }
        return false;
    }



    public boolean keyPressed(int keyCode, int scanCode, int mods) {
        if (!open) return false;

        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            closeModal();
            return true;
        }
        return false;
    }



    private boolean insideModal(int mx, int my) {
        return mx >= modalX && mx <= modalX + modalW &&
               my >= modalY && my <= modalY + modalH;
    }



    private int getIndexAtPosition(int mx, int my) {
        if (!open || currentScale == 0) return -1;

        // float lx = (mx - modalX) / currentScale;
        float ly = (my - modalY) / currentScale;

        // if (lx < 0 || lx > BASE_WIDTH) return -1; // ignore x axis for easier dragging

        float listY = ly - (PADDING + HEADER_HEIGHT);
        if (listY < 0) return -1;

        int index = (int) (listY / ITEM_HEIGHT);
        return (index >= 0 && index < workingList.size()) ? index : -1;
    }



    private boolean isHoveringSave(int mx, int my) {
        float lx = (mx - modalX) / currentScale;
        float ly = (my - modalY) / currentScale;

        int bx = 20;
        int bw = BASE_WIDTH - 40;
        int bh = 20;

        return lx >= bx && lx <= bx + bw &&
               ly >= saveBtnY && ly <= saveBtnY + bh;
    }

    

    private void saveAndClose() {
        if (originalList != null && workingList != null) {
            originalList.clear();
            originalList.addAll(workingList);
        }
        closeModal();
    }
}

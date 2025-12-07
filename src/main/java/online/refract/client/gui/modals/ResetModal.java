// package online.refract.client.gui.modals;

// import org.lwjgl.glfw.GLFW;

// import net.minecraft.client.font.TextRenderer;
// import net.minecraft.client.gui.DrawContext;
// import net.minecraft.text.Text;

// public class ResetModal {

//     // layout constants
//     private static final int WIDTH = 200;
//     private static final int HEIGHT = 80;
    
//     // colors
//     private static final int BG_DIM = 0x80000000;
//     private static final int MODAL_BG = 0xFF202020;
//     private static final int MODAL_BORDER = 0xFFFFFFFF;
//     private static final int BTN_COLOR = 0xFF404040;
//     private static final int BTN_HOVER = 0xFF606060;
//     private static final int TEXT_WARNING = 0xFFFF5555; // Red color for warning

//     // state
//     private boolean open = false;
//     private Runnable onResetAction;

//     // calculated positions
//     private int modalX, modalY;
    
//     // button layout
//     private int btnY;
//     private int btnResetX, btnCancelX;
//     private int btnW, btnH;

//     public void init() {
//         this.open = false;
//         this.onResetAction = null;
//     }

//     /**
//      * Opens the modal.
//      * @param onReset The function to run if the user clicks Reset.
//      */
//     public void openModal(Runnable onReset) {
//         this.onResetAction = onReset;
//         this.open = true;
//     }

//     public void closeModal() {
//         this.init();
//     }

//     public boolean isOpen() { return open; }

//     public void render(DrawContext ctx, TextRenderer tr, int mouseX, int mouseY, float delta, int screenW, int screenH) {
//         if (!open) return;
//         modalX = (screenW - WIDTH) / 2;
//         modalY = (screenH - HEIGHT) / 2;

//         btnW = 80;
//         btnH = 20;
//         btnY = HEIGHT - 30;
        
//         // Spacing buttons evenly
//         int gap = (WIDTH - (btnW * 2)) / 3;
//         btnResetX = gap;
//         btnCancelX = gap + btnW + gap;

//         ctx.fillGradient(0, 0, screenW, screenH, BG_DIM, BG_DIM);
//         ctx.fill(modalX, modalY, WIDTH, HEIGHT, MODAL_BG);
//         ctx.drawBorder(modalX, modalY, WIDTH, HEIGHT, MODAL_BORDER);

//         ctx.drawCenteredTextWithShadow(tr, Text.of("Warning, this will reset all scores"), 
//                 screenW / 2, 20, TEXT_WARNING);

//         drawButton(ctx, tr, "Reset", btnResetX, btnY, mouseX, mouseY);
//         drawButton(ctx, tr, "Cancel", btnCancelX, btnY, mouseX, mouseY);

//     }


//     private void drawButton(DrawContext ctx, TextRenderer tr, String label, int x, int y, int mx, int my) {
//         boolean hovered = isHoveringButton(x, y, mx, my);
//         int color = hovered ? BTN_HOVER : BTN_COLOR;

//         ctx.fill(x, y, x + btnW, y + btnH, color);
//         ctx.drawBorder(x, y, btnW, btnH, 0xFFAAAAAA);
//         ctx.drawCenteredTextWithShadow(tr, Text.of(label), 
//                 x + (btnW / 2), y + 6, 0xFFFFFFFF);
//     }

//     public boolean mouseClicked(int mx, int my, int button, int w, int h) {
//         if (!open) return false;

//         // Check Reset Click
//         if (isHoveringButton(btnResetX, btnY, mx, my)) {
//             if (onResetAction != null) {
//                 onResetAction.run();
//             }
//             closeModal();
//             return true;
//         }

//         // Check Cancel Click
//         if (isHoveringButton(btnCancelX, btnY, mx, my)) {
//             closeModal();
//             return true;
//         }

//         // Click outside closes modal (Optional, remove if you want to force a button press)
//         if (!insideModal(mx, my)) {
//             closeModal();
//             return true;
//         }

//         return true; // Consume click if inside modal but not on button
//     }

//     public boolean keyPressed(int keyCode, int scanCode, int mods) {
//         if (!open) return false;

//         if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
//             closeModal();
//             return true;
//         }
//         return false;
//     }

//     private boolean insideModal(int mx, int my) {
//         return mx >= modalX && mx <= modalX + WIDTH &&
//                my >= modalY && my <= modalY + HEIGHT;
//     }

//     private boolean isHoveringButton(int btnX, int btnY, int mx, int my) {
//         // Transform mouse coordinates to local modal coordinates
//         int localMx = mx - modalX;
//         int localMy = my - modalY;

//         return localMx >= btnX && localMx <= btnX + btnW &&
//                localMy >= btnY && localMy <= btnY + btnH;
//     }
// }
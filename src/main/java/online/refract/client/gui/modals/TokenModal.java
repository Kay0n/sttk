package online.refract.client.gui.modals;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import online.refract.client.gui.PlayerToken;



public class TokenModal {

    private static final int MODAL_WIDTH = 140;
    private static final int MODAL_HEIGHT = 150;    

    private PlayerToken selectedPlayer = null;
    private final List<ButtonWidget> popupButtons = new ArrayList<>();



    public void renderModal(DrawContext context, TextRenderer textRenderer, int virtualMouseX, int virtualMouseY, float delta, int w, int h) {
        if (selectedPlayer == null){ return; }

        context.fillGradient(0, 0, w, h, 0x50000000, 0x50000000); 
        int x = (w / 2) - (MODAL_WIDTH / 2);
        int y = (h / 2) - (MODAL_HEIGHT / 2);
        context.fill(x, y, x + MODAL_WIDTH, y + MODAL_HEIGHT, 0xFF202020);
        context.drawBorder(x, y, MODAL_WIDTH, MODAL_HEIGHT, 0xFFFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer, Text.of(selectedPlayer.name), w / 2, y + 6, 0xFFFFFFFF);
        
        for (ButtonWidget btn : popupButtons) {
            btn.render(context, virtualMouseX, virtualMouseY, delta);
        }
    }



    public void openModal(PlayerToken player, int w, int h) {
        this.selectedPlayer = player;
        this.popupButtons.clear();

        int centerX = w / 2;
        int centerY = h / 2;
        
        int startX = centerX - (120 / 2); 
        int startY = centerY - (MODAL_HEIGHT / 2) + 20; 
        int gap = 22;

        addModalBtn("🏠", "Home",          startX, startY,           "Home");
        addModalBtn("☠️", "Kill/Revive",   startX, startY + gap,     "Kill/Revive");
        addModalBtn("👈", "Nominate",      startX, startY + gap * 2, "Nominate");
        addModalBtn("🪢", "On The Block",  startX, startY + gap * 3, "On The Block");
        addModalBtn("✔️", "Ghost Vote",    startX, startY + gap * 4, "Ghost Vote");
    }


    public void closeModal() {
        this.selectedPlayer = null;
        this.popupButtons.clear();
    }


    private void addModalBtn(String emoji, String text, int x, int y, String actionName) {
        Text btnText = Text.literal(emoji + " ")
                .append(Text.literal(text).formatted(Formatting.BOLD));

        ButtonWidget btn = ButtonWidget.builder(btnText, b -> {
            if (selectedPlayer != null) {
                // debug("§b[ST] §f " + selectedPlayer.name + " " + actionName);
            }
        }).dimensions(x, y, 120, 20).build();

        this.popupButtons.add(btn);
    }


    public boolean handleModalClicked(int virtualX, int virtualY, int button, int virtualWidth, int virtualHeight) {
        if (selectedPlayer != null) {
            for (ButtonWidget btn : popupButtons) {
                if (btn.mouseClicked(virtualX, virtualY, button)){
                    closeModal();
                    return true;
                } 
            }

            int modalX = (virtualWidth / 2) - (MODAL_WIDTH / 2);
            int modalY = (virtualHeight / 2) - (MODAL_HEIGHT / 2);
            
            boolean isMouseOver = 
                virtualX >= modalX && virtualX <= modalX + MODAL_WIDTH &&
                virtualY >= modalY && virtualY <= modalY + MODAL_HEIGHT;

            if (!isMouseOver) {
                closeModal();
                return true; 
            }
            
        }
        return false;
    }


    public boolean keyPressed(int keyCode, int scanCode, int modifiers){
        if (keyCode == 256 && selectedPlayer != null) { 
            closeModal();
            return true;
        }
        return false;
    }





}

package online.refract.client.gui.modals;

import java.util.ArrayList;
import java.util.Collections;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import online.refract.client.ClientActionHandler;
import online.refract.client.gui.PlayerToken;

public class OrderModal extends Modal {

    private static final int BASE_WIDTH = 150;
    private static final int ITEM_HEIGHT = 22;
    private static final int LIST_TOP_OFFSET = 25;
    
    private ArrayList<PlayerToken> originalList;
    private ArrayList<PlayerToken> workingList;

    private int draggingIndex = -1;
    private int dragOffsetY = 0;
    private int dragOffsetX = 0;

    private float currentScale = 1.0f;

    public OrderModal(ClientActionHandler actionHandler) {
        super(actionHandler, "Reorder Players", BASE_WIDTH, 0);
    }

    public void openModal(ArrayList<PlayerToken> tokens) {
        this.originalList = tokens;
        this.workingList = new ArrayList<>(tokens);
        this.draggingIndex = -1;
        
        int listHeight = workingList.size() * ITEM_HEIGHT;
        int requiredHeight = LIST_TOP_OFFSET + listHeight + BUTTON_HEIGHT + (modalMarginY * 2);
        this.height = requiredHeight;
        
        this.x = (this.screenWidth - this.width) / 2;
        this.y = (this.screenHeight - this.height) / 2;

        super.openModal(); 
    }
    
    
    @Override
    protected void rebuildButtons() {
        this.layoutRows.clear();
        this.addButton(createButtonDef(Component.nullToEmpty("Save"), this::saveOrder));
        super.rebuildButtons();
    }

    @Override
    public void render(GuiGraphics context, Font textRenderer, int mouseX, int mouseY, float delta) {
        if (!this.open) return;

        drawDimBackground(context);


        float maxScreenHeight = this.screenHeight - modalMarginY; 
        if (this.height > maxScreenHeight) {
            this.currentScale = maxScreenHeight / (float) this.height;
        } else {
            this.currentScale = 1.0f;
        }

        context.pose().pushMatrix();
        float centerX = this.screenWidth / 2.0f;
        float centerY = this.screenHeight / 2.0f;

        context.pose().translate(centerX, centerY);
        context.pose().scale(currentScale, currentScale);
        context.pose().translate(-centerX, -centerY);

        int scaledMx = getScaledMouseX(mouseX);
        int scaledMy = getScaledMouseY(mouseY);

        drawModal(context);
        drawTitle(context, textRenderer);
        drawContent(context, textRenderer, scaledMx, scaledMy, delta);
        drawButtons(context, scaledMx, scaledMy, delta);

        context.pose().popMatrix();
    }


    private int getScaledMouseX(double rawMx) {
        float centerX = this.screenWidth / 2.0f;
        return (int) ((rawMx - centerX) / currentScale + centerX);
    }

    private int getScaledMouseY(double rawMy) {
        float centerY = this.screenHeight / 2.0f;
        return (int) ((rawMy - centerY) / currentScale + centerY);
    }

    @Override
    public boolean mouseClicked(int mx, int my, int button) {
        if (!isOpen()) return false;

        int scaledMx = getScaledMouseX(mx);
        int scaledMy = getScaledMouseY(my);

        if (super.mouseClicked(scaledMx, scaledMy, button)) {
            return true;
        }

        int index = getIndexAtPosition(scaledMx, scaledMy);
        if (index != -1) {
            this.draggingIndex = index;
            int itemY = this.y + LIST_TOP_OFFSET + (index * ITEM_HEIGHT);
            int itemX = this.x;
            this.dragOffsetY = itemY - scaledMy;
            this.dragOffsetX = itemX - scaledMx;
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        if (draggingIndex != -1) {
            draggingIndex = -1;
            return true;
        }
        return super.mouseReleased(mx, my, button);
    }


    @Override
    protected void drawContent(GuiGraphics context, Font textRenderer, int mouseX, int mouseY, float delta) {
        if (workingList == null) return;

        if (draggingIndex != -1) {
            int hoverIndex = getIndexAtPosition(mouseX, mouseY);
            if (hoverIndex != -1 && hoverIndex != draggingIndex) {
                Collections.swap(workingList, draggingIndex, hoverIndex);
                draggingIndex = hoverIndex;
            }
        }

        int startX = this.x + modalMarginX;
        int nameCenterX = this.x + (this.width / 2);
        int startY = this.y + LIST_TOP_OFFSET;
        int rowWidth = this.width - (modalMarginX * 2);

        for (int i = 0; i < workingList.size(); i++) {
            int itemY = startY + (i * ITEM_HEIGHT);


            if (i == draggingIndex) {
                context.fill(startX, itemY, startX + rowWidth, itemY + ITEM_HEIGHT, 0xFF101010);
                context.renderOutline(startX, itemY, rowWidth, ITEM_HEIGHT, 0xFF555555);
            } else {
                context.drawCenteredString(textRenderer, Component.nullToEmpty(workingList.get(i).name), nameCenterX, itemY + 6, 0xFFFFFFFF);
            }
        }

        if (draggingIndex != -1) {
            PlayerToken token = workingList.get(draggingIndex);
            
            int dragX = mouseX + dragOffsetX; 
            int dragY = mouseY + dragOffsetY; 

            context.fill(dragX, dragY, dragX + this.width , dragY + ITEM_HEIGHT, 0xFF404040);
            context.renderOutline(dragX, dragY, rowWidth, ITEM_HEIGHT, 0xFFFFFFFF);
            context.drawCenteredString(textRenderer, Component.nullToEmpty(token.name), dragX + (this.width / 2), dragY + 7, 0xFFFFFFFF);
        }
    }

    private int getIndexAtPosition(int mx, int my) {
        int listStartX = this.x + modalMarginX;
        int listWidth = this.width - (modalMarginX * 2);
        int listStartY = this.y + LIST_TOP_OFFSET;
        int totalListHeight = workingList.size() * ITEM_HEIGHT;

        if (mx < listStartX || mx > listStartX + listWidth) return -1;
        if (my < listStartY || my > listStartY + totalListHeight) return -1;

        int relativeY = my - listStartY;
        return relativeY / ITEM_HEIGHT;
    }

    private void saveOrder() {
        if (originalList != null && workingList != null) {
            originalList.clear();
            originalList.addAll(workingList);
            closeModal();
        }
    }
}
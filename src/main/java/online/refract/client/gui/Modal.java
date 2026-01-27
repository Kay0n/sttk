package online.refract.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import online.refract.client.ClientActionHandler;

public abstract class Modal {

    // styles
    // protected static final int MARGIN = 10;
    protected static final int VERTICAL_PADDING = 10;
    protected static final int HORIZONTAL_PADDING = 10;
    protected static final int ELEMENT_HEIGHT = 20;
    public static final int BG_DIM = 0x80000000;
    public static final int MODAL_BG = 0xFF202020;
    public static final int MODAL_BORDER = 0xFFFFFFFF;

    // state
    protected boolean open = false;
    protected int width, height, x, y, screenWidth, screenHeight, verticalPadding, horizontalPadding;
    protected String title;
    protected final ClientActionHandler actionHandler;
    
    protected final List<List<LayoutItem>> rowDefinitions = new ArrayList<>();
    protected final List<AbstractWidget> activeWidgets = new ArrayList<>();



    @FunctionalInterface
    protected interface LayoutItem {
        AbstractWidget place(int x, int y, int width, int height);
    }



    public Modal(ClientActionHandler actionHandler, String title, int width, int height) {
        this.actionHandler = actionHandler;
        this.title = title;
        this.width = width;
        this.height = height;
        this.verticalPadding = VERTICAL_PADDING;
        this.horizontalPadding = HORIZONTAL_PADDING;
    }

    public Modal(ClientActionHandler actionHandler, String title, int width, int height, int verticalPadding, int horizontalPadding) {
        this.actionHandler = actionHandler;
        this.title = title;
        this.width = width;
        this.height = height;
        this.verticalPadding = verticalPadding;
        this.horizontalPadding = horizontalPadding;
    }



    public void init(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.x = (screenWidth - this.width) / 2;
        this.y = (screenHeight - this.height) / 2;
        this.open = false;
        this.activeWidgets.clear();
        this.rowDefinitions.clear();
    }



    public void openModal() {
        this.open = true;
        rebuildLayout();
    }



    public void closeModal() {
        this.open = false;
        this.activeWidgets.clear();
    }



    public boolean isOpen() { return this.open; }



    protected void addButton(Component text, Runnable action) {
        addButtonRow(new ButtonData(text, action));
    }



    protected void addButtonRow(ButtonData... buttons) {
        List<LayoutItem> row = new ArrayList<>();
        for (ButtonData b : buttons) {
            row.add((x, y, w, h) -> Button.builder(b.text, btn -> b.action.run()).bounds(x, y, w, h).build());
        }
        this.rowDefinitions.add(row);
    }



    protected void addEditBoxRow(EditBox editBox) {
        editBox.setBordered(true);
        List<LayoutItem> row = new ArrayList<>();
        row.add((x, y, w, h) -> {
            editBox.setX(x); editBox.setY(y); editBox.setWidth(w); editBox.setHeight(h);
            return editBox;
        });
        this.rowDefinitions.add(row);
    }



    public void addSpacerRow() {
        List<LayoutItem> row = new ArrayList<>();
        row.add((x, y, w, h) -> null);
        this.rowDefinitions.add(row);
    }
    


    public record ButtonData(Component text, Runnable action) {}



    protected void rebuildLayout() {
        this.activeWidgets.clear();

        int availableWidth = this.width - (this.horizontalPadding * 2);
        int totalElementHeight = (rowDefinitions.size() * ELEMENT_HEIGHT);
        int totalElementPaddingHeight = ((rowDefinitions.size() - 1) * this.verticalPadding);
        int currentY = (this.y + this.height) - this.verticalPadding - totalElementHeight - totalElementPaddingHeight;

        for (List<LayoutItem> row : rowDefinitions) {
            int numItems = row.size();
            if (numItems == 0) continue;

            int itemWidth = (availableWidth - ((numItems - 1) * this.horizontalPadding)) / numItems;
            int currentX = this.x + this.horizontalPadding;

            for (LayoutItem item : row) {
                AbstractWidget widget = item.place(currentX, currentY, itemWidth, ELEMENT_HEIGHT);
                if (widget != null) {
                    this.activeWidgets.add(widget);
                }
                currentX += itemWidth + this.horizontalPadding;
            }
            currentY += ELEMENT_HEIGHT + this.verticalPadding;
        }
    }



    protected EditBox createEditbox(String title, int maxLength) {
        EditBox editBox = new EditBox(Minecraft.getInstance().font, 0, 0, 0, 0, Component.literal(title));
        editBox.setMaxLength(maxLength);
        return editBox;
    }



    public void render(GuiGraphics context, Font font, int mouseX, int mouseY, float delta) {
        if (!this.open) return;

        context.fillGradient(0, 0, this.screenWidth, this.screenHeight, BG_DIM, BG_DIM);
        context.fill(this.x, this.y, this.x + this.width, this.y + this.height, MODAL_BG);
        context.renderOutline(this.x, this.y, this.width, this.height, MODAL_BORDER);
        context.drawCenteredString(font, this.title, this.x + (this.width / 2), this.y + verticalPadding, 0xFFFFFFFF);

        for (AbstractWidget widget : activeWidgets) {
            widget.render(context, mouseX, mouseY, delta);
        }
    }



    public boolean mouseClicked(int mx, int my, int button) {

        if (!open) return false;

        if (!isInsideModal(mx, my)) {

            closeModal();
            return true;
        }

        for (AbstractWidget widget : activeWidgets) {
            if (widget.mouseClicked(mx, my, button)) {
                if (widget instanceof Button) {
                    closeModal(); 
                } 
                else if (widget instanceof EditBox) {
                    activeWidgets.stream()
                        .filter(w -> w != widget && w instanceof EditBox)
                        .forEach(w -> w.setFocused(false));
                }
                return true;
            }
        }
        
        activeWidgets.forEach(w -> w.setFocused(false));
        return true;
    }



    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!open) return false;
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            closeModal();
            return true;
        }
        for (AbstractWidget widget : activeWidgets) {
            if (widget.keyPressed(keyCode, scanCode, modifiers)) return true;
        }
        return false;
    }



    public boolean charTyped(char chr, int modifiers) {
        if (!open) return false;
        for (AbstractWidget widget : activeWidgets) {
            if (widget.charTyped(chr, modifiers)) return true;
        }
        return false;
    }
    


    public boolean mouseReleased(double mx, double my, int button) { return false; }



    protected boolean isInsideModal(int mx, int my) {
        return 
            mx >= this.x && mx <= this.x + this.width && 
            my >= this.y && my <= this.y + this.height;
    }
}
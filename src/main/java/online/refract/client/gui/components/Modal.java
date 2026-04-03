package online.refract.client.gui.components;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

import org.lwjgl.glfw.GLFW;
import online.refract.client.ClientActionHandler;

public abstract class Modal {

    public static final int BG_DIM = 0x80000000;
    public static final int MODAL_BG = 0xFF202020;
    public static final int MODAL_BORDER = 0xFFFFFFFF;

    private static final int DEFAULT_MARGIN = 12;
    private static final int DEFAULT_GAP = 8;
    private static final int ELEMENT_HEIGHT = 20;

    protected boolean open = false;
    protected int width, height, x, y, screenWidth, screenHeight;
    protected int margin, gap;
    protected String title;
    protected final ClientActionHandler actionHandler;
    protected Font font;
    
    private final List<RowData> rows = new ArrayList<>();
    private final List<AbstractWidget> modalWidgets = new ArrayList<>();
    protected AbstractWidget focusedWidget = null;

    protected record RowData(List<AbstractWidget> widgets, int height) {}


    public Modal(ClientActionHandler actionHandler, String title, int width, int margin, int gap) {
        this.actionHandler = actionHandler;
        this.title = title;
        this.width = width;
        this.margin = margin;
        this.gap = gap;
    }

    public Modal(ClientActionHandler actionHandler, String title, int width) {
        this(actionHandler, title, width, DEFAULT_MARGIN, DEFAULT_GAP);
    }



    public void init(int screenWidth, int screenHeight, Font font) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.open = false;
        this.modalWidgets.clear();
        this.rows.clear();
        this.font = font;
    }

    public void openModal() {
        this.open = true;
        rebuildLayout();
    }

    public void closeModal() {
        this.open = false;
        this.clearFocus();
        this.modalWidgets.clear();

    }

    public boolean isOpen() { return this.open; }


    // ==== Widgets ====

    protected Button createButton(Component text, Runnable action) {
        return Button.builder(text, btn -> action.run()).build();
    }

    protected EditBox createEditBox(String placeholder, int maxLength, String value) {
        EditBox editBox = createEditBox(placeholder, maxLength);
        editBox.setValue(value);
        return editBox;
    }

    protected EditBox createEditBox(String placeholder, int maxLength) {
        EditBox editBox = new EditBox(Minecraft.getInstance().font, 0, 0, 0, ELEMENT_HEIGHT, Component.literal(placeholder));
        editBox.setMaxLength(maxLength);
        editBox.setHint(Component.literal(placeholder));
        return editBox;
    }


    protected void addButton(Component text, Runnable action) {
        addButtonRow(createButton(text, action));
    }

    protected void addButtonRow(Button... buttons) {
        this.rows.add(new RowData(List.of(buttons), ELEMENT_HEIGHT));
    }

    protected void addEditBoxRow(EditBox editBox) {
        editBox.setBordered(true);
        this.rows.add(new RowData(List.of(editBox), ELEMENT_HEIGHT));
    }

    protected void addCustomRow(int height, AbstractWidget... widgets) {
        this.rows.add(new RowData(List.of(widgets), height));
    }

    protected void addSpacerRow() {
        this.rows.add(new RowData(List.of(), 0));
    }

    protected <T> void addSelectionRow(SelectionWidget<T> widget, int height) {
        widget.setHeight(height); 
        this.rows.add(new RowData(List.of(widget), height));
    }

    protected void clearWidgets() {
        this.rows.clear();
        this.modalWidgets.clear();
        this.focusedWidget = null;
    }




    private int measureHeight() {
        int total = (this.margin * 2) + this.font.lineHeight + this.gap; // title area

        for (RowData row : rows) {
            if (row.widgets().isEmpty()) {
                total += (ELEMENT_HEIGHT / 2) + this.gap; // spacer
            } else {
                total += row.height() + this.gap;
            }
        }
        return total;
    }

    protected void rebuildLayout() {
        this.modalWidgets.clear();
        
        this.height = measureHeight();
        this.x = (screenWidth - this.width) / 2;
        this.y = (screenHeight - this.height) / 2;

        int availableWidth = this.width - (this.margin * 2);
        int currentY = this.y + this.margin + this.font.lineHeight + this.gap;

        for (RowData row : rows) {
            if (row.widgets().isEmpty()) {
                currentY += (ELEMENT_HEIGHT / 2) + this.gap;
                continue;
            }

            int numItems = row.widgets().size();
            int totalGapsWidth = (numItems - 1) * this.gap;
            int itemWidth = (availableWidth - totalGapsWidth) / numItems;
            int currentX = this.x + this.margin;

            for (AbstractWidget widget : row.widgets()) {
                widget.setX(currentX);
                widget.setY(currentY);
                widget.setWidth(itemWidth);
                widget.setHeight(row.height());
                this.modalWidgets.add(widget);
                currentX += itemWidth + this.gap;
            }
            currentY += row.height() + this.gap;
        }

        if (this.focusedWidget != null && modalWidgets.contains(this.focusedWidget)) {
            setFocus(this.focusedWidget);
        }
    }


    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        if (!this.open) return;

        context.fillGradient(0, 0, this.screenWidth, this.screenHeight, BG_DIM, BG_DIM);
        context.fill(this.x, this.y, this.x + this.width, this.y + this.height, MODAL_BG);
        context.renderOutline(this.x, this.y, this.width, this.height, MODAL_BORDER);
        context.drawCenteredString(this.font, this.title, this.x + (this.width / 2), this.y + this.margin, 0xFFFFFFFF);

        for (AbstractWidget widget : modalWidgets) {
            widget.render(context, mouseX, mouseY, delta);
        }
    }


    private boolean isInsideModal(int mx, int my) {
        return mx >= this.x && mx <= this.x + this.width && 
               my >= this.y && my <= this.y + this.height;
    }


    protected void setFocus(AbstractWidget focusedWidget) {
        this.focusedWidget = focusedWidget;
        for (AbstractWidget w : modalWidgets) {
            w.setFocused(w == focusedWidget);
        }
        if (this.focusedWidget instanceof EditBox) {
            ((EditBox) this.focusedWidget).moveCursorToEnd(false);
        }
    }

    protected void clearFocus() {
         this.focusedWidget = null;
        for (AbstractWidget w : modalWidgets) {
            w.setFocused(false);
        }
    }


    private void cycleFocus(boolean reverse) {
        if (modalWidgets.isEmpty()) return;

        int currentIndex = modalWidgets.indexOf(this.focusedWidget);

        int size = modalWidgets.size();
        int nextIndex = currentIndex;

        for (int i = 0; i < size; i++) {
            if (reverse) {
                nextIndex--;
                if (nextIndex < 0) nextIndex = size - 1;
            } else {
                nextIndex++;
                if (nextIndex >= size) nextIndex = 0;
            }

            AbstractWidget widget = modalWidgets.get(nextIndex);
            
            if (widget.active && widget.visible) {
                setFocus(widget);
                return;
            }
        }
    }





    // ==== Event Handling ====

    public boolean mouseClicked(int mx, int my, int button) {
        if (!open) return false;
        if (!isInsideModal(mx, my)) {
            closeModal();
            return true;
        }

        boolean clickedWidget = false;
        
        // loop backwards to prioritize topmost widgets in case of overlap
        for (int i = modalWidgets.size() - 1; i >= 0; i--) {
            AbstractWidget widget = modalWidgets.get(i);
            if (widget.mouseClicked(mx, my, button)) {
                clickedWidget = true;
                setFocus(widget);
                break; 
            }
        }
        
        if (!clickedWidget) setFocus(null);
        return true;
    }


    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!open) return false;
        for (AbstractWidget widget : modalWidgets) {
            if (widget.mouseReleased(mouseX, mouseY, button)) return true;
        }
        return false;
    }


    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!open) return false;
        for (AbstractWidget widget : modalWidgets) {
            if (widget.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) return true;
        }
        return false;
    }


    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!open) return false;
        for (AbstractWidget widget : modalWidgets) {
            if (widget.mouseDragged(mouseX, mouseY, button, dragX, dragY)) return true;
        }
        return false;
    }





    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!open) return false;
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            closeModal();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_TAB) {
            boolean shiftPressed = (modifiers & GLFW.GLFW_MOD_SHIFT) != 0;
            cycleFocus(shiftPressed); 
            return true;
        }
        for (AbstractWidget widget : modalWidgets) {
            if (widget.isFocused() && widget.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }


    public boolean charTyped(char chr, int modifiers) {
        if (!open) return false;
        for (AbstractWidget widget : modalWidgets) {
            if (widget.charTyped(chr, modifiers)) return true;
        }
        return false;
    }






}
package online.refract.client.gui.objects;

import java.util.ArrayList;
import java.util.List;

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

    public static final int BG_DIM = 0x80000000;
    public static final int MODAL_BG = 0xFF202020;
    public static final int MODAL_BORDER = 0xFFFFFFFF;

    protected static final int DEFAULT_MARGIN = 12;
    protected static final int DEFAULT_GAP = 8;
    protected static final int ELEMENT_HEIGHT = 20;

    protected boolean open = false;
    protected int width, height, x, y, screenWidth, screenHeight;
    protected int margin, gap;
    protected String title;
    protected final ClientActionHandler actionHandler;
    
    protected final List<RowData> rows = new ArrayList<>();
    protected final List<AbstractWidget> activeWidgets = new ArrayList<>();
    protected AbstractWidget focusedWidget = null;

    // height of -1 means "expand to fill remaining space"
    protected record RowData(List<AbstractWidget> widgets, int height) {}


    public Modal(ClientActionHandler actionHandler, String title, int width, int height, int margin, int gap) {
        this.actionHandler = actionHandler;
        this.title = title;
        this.width = width;
        this.height = height;
        this.margin = margin;
        this.gap = gap;
    }

    public Modal(ClientActionHandler actionHandler, String title, int width, int height) {
        this(actionHandler, title, width, height, DEFAULT_MARGIN, DEFAULT_GAP);
    }



    public void init(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.x = (screenWidth - this.width) / 2;
        this.y = (screenHeight - this.height) / 2;
        this.open = false;
        this.activeWidgets.clear();
        this.rows.clear();
    }

    public void openModal() {
        this.open = true;
        rebuildLayout();
    }

    public void closeModal() {
        this.open = false;
        this.activeWidgets.clear();
        this.focusedWidget = null;
    }

    public boolean isOpen() { return this.open; }


    // ==== Widgets ====

    protected Button createButton(Component text, Runnable action) {
        return Button.builder(text, btn -> action.run()).build();
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

    protected void addExpandingRow(AbstractWidget... widgets) {
        this.rows.add(new RowData(List.of(widgets), -1));
    }

    protected void addCustomRow(int height, AbstractWidget... widgets) {
        this.rows.add(new RowData(List.of(widgets), height));
    }

    public void addSpacerRow() {
        this.rows.add(new RowData(List.of(), 0));
    }

    protected EditBox createEditbox(String title, int maxLength) {
        EditBox editBox = new EditBox(Minecraft.getInstance().font, 0, 0, 0, ELEMENT_HEIGHT, Component.literal(title));
        editBox.setMaxLength(maxLength);
        return editBox;
    }


    // ==== Lifecyle ====

    protected void rebuildLayout() {
        this.activeWidgets.clear();

        Font font = Minecraft.getInstance().font;
        int availableWidth = this.width - (this.margin * 2);
        
        int availableHeightForContent = this.height - (this.margin * 2) - font.lineHeight - this.gap;
        int fixedHeightTotal = 0;
        int expandingRowsCount = 0;

        for (RowData row : rows) {
            if (row.widgets().isEmpty()) { 
                fixedHeightTotal += (ELEMENT_HEIGHT / 2) + gap;
            } else if (row.height() > 0) { 
                fixedHeightTotal += row.height() + gap;
            } else if (row.height() == -1) {
                expandingRowsCount++;
                fixedHeightTotal += gap; 
            }
        }

        int expandHeight = expandingRowsCount > 0 ? 
            Math.max(0, (availableHeightForContent - fixedHeightTotal) / expandingRowsCount) : 0;

        int currentY = this.y + this.margin + font.lineHeight + this.gap;

        for (RowData row : rows) {
            if (row.widgets().isEmpty()) {
                currentY += (ELEMENT_HEIGHT / 2) + this.gap;
                continue;
            }

            int rowHeight = row.height() > 0 ? row.height() : expandHeight;
            
            int numItems = row.widgets().size();
            int totalGapsWidth = (numItems - 1) * this.gap;
            int itemWidth = (availableWidth - totalGapsWidth) / numItems;
            int currentX = this.x + this.margin;

            for (AbstractWidget widget : row.widgets()) {
                widget.setX(currentX);
                widget.setY(currentY);
                widget.setWidth(itemWidth);
                widget.setHeight(rowHeight);
                
                this.activeWidgets.add(widget);
                currentX += itemWidth + this.gap;
            }
            currentY += rowHeight + this.gap;
        }
    }


    public void render(GuiGraphics context, Font font, int mouseX, int mouseY, float delta) {
        if (!this.open) return;

        context.fillGradient(0, 0, this.screenWidth, this.screenHeight, BG_DIM, BG_DIM);
        context.fill(this.x, this.y, this.x + this.width, this.y + this.height, MODAL_BG);
        context.renderOutline(this.x, this.y, this.width, this.height, MODAL_BORDER);
        context.drawCenteredString(font, this.title, this.x + (this.width / 2), this.y + this.margin, 0xFFFFFFFF);

        for (AbstractWidget widget : activeWidgets) {
            widget.render(context, mouseX, mouseY, delta);
        }
    }


    protected boolean isInsideModal(int mx, int my) {
        return mx >= this.x && mx <= this.x + this.width && 
               my >= this.y && my <= this.y + this.height;
    }


    private void setFocus(AbstractWidget focusedWidget) {
        this.focusedWidget = focusedWidget;
        for (AbstractWidget w : activeWidgets) {
            w.setFocused(w == focusedWidget);
        }
    }


private void cycleFocus(boolean reverse) {
        if (activeWidgets.isEmpty()) return;

        int currentIndex = activeWidgets.indexOf(this.focusedWidget);

        int size = activeWidgets.size();
        int nextIndex = currentIndex;

        for (int i = 0; i < size; i++) {
            if (reverse) {
                nextIndex--;
                if (nextIndex < 0) nextIndex = size - 1;
            } else {
                nextIndex++;
                if (nextIndex >= size) nextIndex = 0;
            }

            AbstractWidget widget = activeWidgets.get(nextIndex);
            
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
        for (int i = activeWidgets.size() - 1; i >= 0; i--) {
            AbstractWidget widget = activeWidgets.get(i);
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
        for (AbstractWidget widget : activeWidgets) {
            if (widget.mouseReleased(mouseX, mouseY, button)) return true;
        }
        return false;
    }


    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!open) return false;
        for (AbstractWidget widget : activeWidgets) {
            if (widget.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) return true;
        }
        return false;
    }


    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!open) return false;
        for (AbstractWidget widget : activeWidgets) {
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
        for (AbstractWidget widget : activeWidgets) {
            if (widget.keyPressed(keyCode, scanCode, modifiers)) return true;
        }

        if (keyCode == GLFW.GLFW_KEY_TAB) {
            boolean shiftPressed = (modifiers & GLFW.GLFW_MOD_SHIFT) != 0;
            cycleFocus(shiftPressed); 
            return true;
        }
        for (AbstractWidget widget : activeWidgets) {
            if (widget.isFocused()) {
                if (widget.keyPressed(keyCode, scanCode, modifiers)) {
                    return true;
                }
            }
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






}
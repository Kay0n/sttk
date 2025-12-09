package online.refract.client.gui.modals;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import online.refract.client.ClientActionHandler;

public abstract class Modal {

    protected int modalMarginX = 10;
    protected int modalMarginY = 10;
    protected int elementMarginX = 10;
    protected int elementMarginY = 10;

    protected static final int BUTTON_HEIGHT = 20;
    private static final int BG_DIM = 0x80000000;
    private static final int MODAL_BG = 0xFF202020;
    private static final int MODAL_BORDER = 0xFFFFFFFF;
    private static final int TITLE_COLOR = 0xFFFFFFFF;

    protected boolean open = false;
    protected ClientActionHandler actionHandler;
    protected List<ButtonWidget> buttons = new ArrayList<>();
    protected List<List<RowElement>> layoutRows = new ArrayList<>();
    protected List<TextFieldWidget> textFields = new ArrayList<>();    

    protected int width;
    protected int height;
    protected int screenWidth;
    protected int screenHeight;
    protected int x;
    protected int y;
    protected String title;
    
    protected record ButtonDefinition(Text text, Runnable action, boolean isSpacer) {}



    public Modal(ClientActionHandler actionHandler, String title, int width, int height) {
        this.actionHandler = actionHandler;
        this.width = width;
        this.height = height;
        this.title = title;
    }

    public void init(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.x = (screenWidth / 2) - (this.width / 2);
        this.y = (screenHeight / 2) - (this.height / 2);
        this.open = false;
        this.buttons.clear();
        this.layoutRows.clear();
        this.textFields.clear();
        for (TextFieldWidget tf : textFields) tf.setFocused(false);

    }

    public void openModal(){
        this.open = true;
        rebuildButtons();
    }

    public void closeModal(){
        this.open = false;
        this.buttons.clear();
        for (TextFieldWidget tf : textFields) tf.setFocused(false);

    }

    public boolean isOpen(){
        return this.open;
    }

    protected void addButton(ButtonDefinition button) {
        List<RowElement> row = new ArrayList<>();
        row.add(RowElement.button(button));
        this.layoutRows.add(row);
    }


    protected void addButtonRow(ButtonDefinition... defs) {
        List<RowElement> row = new ArrayList<>();
        for (ButtonDefinition def : defs) {
            row.add(RowElement.button(def));
        }
        this.layoutRows.add(row);
    }

    public void addVerticalSpacer(int size) {
        List<RowElement> row = new ArrayList<>();
        row.add(RowElement.spacer(size));
        this.layoutRows.add(row);
    }

    protected void addTextFieldRow(TextFieldWidget tf) {
        tf.setDrawsBackground(true);
        this.textFields.add(tf);

        List<RowElement> row = new ArrayList<>();
        row.add(RowElement.textField(tf));
        this.layoutRows.add(row);
    }

    protected ButtonDefinition createButtonDef(Text text, Runnable action) {
        return new ButtonDefinition(text, action, false);
    }

    protected void rebuildButtons() {
        this.buttons.clear();

        int availableWidth = this.width - (modalMarginX * 2);
        int totalHeight = 0;

        for (List<RowElement> row : layoutRows) {
            RowElement e = row.get(0);

            switch (e.type) {
                case BUTTON -> totalHeight += BUTTON_HEIGHT;
                case TEXT_FIELD -> totalHeight += BUTTON_HEIGHT;
                case SPACER -> totalHeight += e.spacerSize;
            }
        }
        totalHeight += (layoutRows.size() - 1) * elementMarginY;

        int currentY = (this.y + this.height) - modalMarginY - totalHeight;

        for (List<RowElement> row : layoutRows) {
            // SPACER ROW
            if (row.get(0).type == RowElement.Type.SPACER) {
                currentY += row.get(0).spacerSize + elementMarginY;
                continue;
            }

            // TEXT FIELD ROW
            if (row.get(0).type == RowElement.Type.TEXT_FIELD) {
                TextFieldWidget tf = row.get(0).textField;
                int fieldWidth = availableWidth;
                int fieldX = this.x + modalMarginX;

                tf.setX(fieldX);
                tf.setY(currentY);
                tf.setWidth(fieldWidth);
                tf.setHeight(BUTTON_HEIGHT);

                currentY += BUTTON_HEIGHT + elementMarginY;
                continue;
            }

            // BUTTON ROW
            int numButtons = row.size();
            int totalGapWidth = (numButtons - 1) * elementMarginX;
            int buttonWidth = (availableWidth - totalGapWidth) / numButtons;

            int currentX = this.x + modalMarginX;

            for (RowElement elem : row) {
                ButtonDefinition def = elem.button;

                ButtonWidget btn = ButtonWidget.builder(def.text, b -> def.action.run())
                        .dimensions(currentX, currentY, buttonWidth, BUTTON_HEIGHT)
                        .build();

                this.buttons.add(btn);
                currentX += buttonWidth + elementMarginX;
            }

            currentY += BUTTON_HEIGHT + elementMarginY;
        }
    }

    public void render(DrawContext context, TextRenderer textRenderer, int mouseX, int mouseY, float delta){
        if (!this.open) return;
        
        drawDimBackground(context);
        drawModal(context);
        drawTitle(context, textRenderer);
        drawContent(context, textRenderer, mouseX, mouseY, delta);
        drawTextFields(context, mouseX, mouseY, delta);
        drawButtons(context, mouseX, mouseY, delta);


    }

    public void drawDimBackground(DrawContext context){
        context.fillGradient(0, 0, this.screenWidth, this.screenHeight, BG_DIM, BG_DIM); 

    }
    public void drawModal(DrawContext context){
        context.fill(this.x, this.y, this.x + this.width, this.y + this.height, MODAL_BG);
        context.drawBorder(this.x, this.y, this.width, this.height, MODAL_BORDER);
    }
    public void drawTitle(DrawContext context, TextRenderer textRenderer){
        context.drawCenteredTextWithShadow(
            textRenderer, 
            this.title, 
            this.x + (this.width / 2), 
            this.y + modalMarginY, 
            TITLE_COLOR
        );
        
    }

    public void drawButtons(DrawContext context, int mouseX, int mouseY, float delta){
        for (ButtonWidget btn : buttons) {
            btn.render(context, mouseX, mouseY, delta);
        }
    }

    protected void drawTextFields(DrawContext context, int mouseX, int mouseY, float delta){
        for (TextFieldWidget tf : textFields) {
            tf.render(context, mouseX, mouseY, delta);
        }
    }

    protected void drawContent(DrawContext context, TextRenderer textRenderer, int mouseX, int mouseY, float delta) {
    }



    public boolean mouseClicked(int mx, int my, int button) {
        if (!open) return false;

        if (!isInsideModal(mx, my)) {
            closeModal();
            return true;
        }

        for (TextFieldWidget tf : textFields) {
            if (tf.mouseClicked(mx, my, button)) {
                tf.setFocused(true);
                return true; 
            } else {
                tf.setFocused(false);
            }
        }

        for (ButtonWidget btn : buttons) {
            if (btn.mouseClicked(mx, my, button)){
                closeModal();
                for (TextFieldWidget tf : textFields) tf.setFocused(false);
                return true;
            } 
        }

        return false; 
    }

    public boolean mouseReleased(double mx, double my, int button) {
        if (!open) return false;
        return false;
    }




    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (TextFieldWidget tf : textFields) {
            if (tf.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }

        if (keyCode == GLFW.GLFW_KEY_ESCAPE && this.open) {
            closeModal();
            return true;
        }

        return false;
    }

    public boolean charTyped(char chr, int modifiers) {
        if (!this.open) return false;
        for (TextFieldWidget tf : textFields) {
            if (tf.charTyped(chr, modifiers)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isInsideModal(int x, int y) {
        return x >= this.x && x <= this.x + this.width &&
               y >= this.y && y <= this.y + this.height;
    }



    protected static class RowElement {
        enum Type { BUTTON, SPACER, TEXT_FIELD }

        final Type type;
        final ButtonDefinition button;
        final TextFieldWidget textField;
        final int spacerSize;


        RowElement(Type type, ButtonDefinition button, TextFieldWidget textField, int spacerSize) {
            this.type = type;
            this.button = button;
            this.textField = textField;
            this.spacerSize = spacerSize;
        }

        static RowElement button(ButtonDefinition def) {
            return new RowElement(Type.BUTTON, def, null, 0);
        }

        static RowElement spacer(int size) {
            return new RowElement(Type.SPACER, null, null, size);
        }

        static RowElement textField(TextFieldWidget tf) {
            return new RowElement(Type.TEXT_FIELD, null, tf, 0);
        }
    }
    
}

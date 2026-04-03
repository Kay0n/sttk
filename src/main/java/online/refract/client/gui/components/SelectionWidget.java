package online.refract.client.gui.components;

import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;

public class SelectionWidget<T> extends ObjectSelectionList<SelectionWidget<T>.Entry> {

    private final Function<T, String> nameProvider;

    public SelectionWidget(Function<T, String> nameProvider) {
        super(Minecraft.getInstance(), 0, 0, 0, 20); // size set externally
        this.nameProvider = nameProvider;
    }

    public void addEntry(T item) {
        super.addEntry(new Entry(item));
    }

    public void clearEntries() {
        super.clearEntries();
    }

    public T getSelectedValue() {
        Entry selected = this.getSelected();
        return selected != null ? selected.getValue() : null;
    }

    @Override
    public int getRowWidth() {
        return this.width - 20; // align scrollbar with modal
    }

    public class Entry extends ObjectSelectionList.Entry<Entry> {
        private final T value;

        public Entry(T value) {
            this.value = value;
        }

        public T getValue() {
            return value;
        }

        public String getName() {
            return nameProvider.apply(value);
        }

        @Override
        public void render(GuiGraphics graphics, int index, int top, int left,
                           int width, int height, int mouseX, int mouseY,
                           boolean hovered, float delta) {

            graphics.drawString(
                Minecraft.getInstance().font,
                getName(),
                left + 5,
                top + 5,
                0xFFFFFFFF
            );
        }

        @Override
        public Component getNarration() {
            return Component.literal(getName());
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0
                && SelectionWidget.this.isMouseOver(mouseX, mouseY)
                && SelectionWidget.this.getEntryAtPosition(mouseX, mouseY) == this) {

                SelectionWidget.this.setSelected(this);
                return true;
            }
            return false;
        }
    }
}
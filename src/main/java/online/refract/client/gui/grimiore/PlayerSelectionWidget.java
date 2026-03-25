package online.refract.client.gui.grimiore;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;

public class PlayerSelectionWidget extends ObjectSelectionList<PlayerSelectionWidget.PlayerEntry> {
    
    public PlayerSelectionWidget(Minecraft minecraft) {
        super(minecraft, 0, 0, 0, 20); // width and height dynamically set by Modal later
    }

    public void addPlayer(String playerName) {
        this.addEntry(new PlayerEntry(playerName));
    }

    @Override
    public int getRowWidth() {
        return this.width - 20; // align scrollbar with modal
    }

    // @Override
    // public boolean isMouseOver(double mouseX, double mouseY) {
    //     return mouseX >= this.getX() && mouseX <= this.getX() + this.width &&
    //            mouseY >= this.getY() && mouseY <= this.getY() + this.height;
    // }

    // @Override
    // 	protected int scrollBarX() {
    //     return this.getX() + this.width - 6; 
    // }

    public class PlayerEntry extends ObjectSelectionList.Entry<PlayerEntry> {
        private final String playerName;

        public PlayerEntry(String playerName) {
            this.playerName = playerName;
        }

        public String getName() {
            return playerName;
        }

        @Override
        public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float delta) {
            graphics.drawString(Minecraft.getInstance().font, this.playerName, left + 5, top + 5, 0xFFFFFFFF);
        }

        @Override
        public Component getNarration() {
            return Component.literal(playerName);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {

            if (button == 0 
                && PlayerSelectionWidget.this.isMouseOver(mouseX, mouseY) 
                && PlayerSelectionWidget.this.getEntryAtPosition(mouseX, mouseY) == this) {
                
                PlayerSelectionWidget.this.setSelected(this);
                return true; 
            }
            return false; 
        }
    }
}
package online.refract.client.gui.screens.script;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import online.refract.client.ClientAssetCache;
import online.refract.game.state.ClocktowerRole;
import online.refract.game.state.Enums.RoleType;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class RoleListWidget extends ObjectSelectionList<RoleListWidget.Entry> {
    private final int cardHeight;
    private final int cardAreaWidth;

    public RoleListWidget(Minecraft minecraft, int width, int height, int y, int cardHeight, int cardAreaWidth) {
        super(minecraft, width, height, y, cardHeight);
        this.cardHeight    = cardHeight;
        this.cardAreaWidth = cardAreaWidth;
    }

    public void populate(String editionName, List<ClocktowerRole> roles, ClientAssetCache assetCache) {
        clearEntries();
        addEntry(new TitleEntry(editionName, cardAreaWidth, cardHeight));
        for (RoleType type : RoleType.values()) {
            List<ClocktowerRole> ofType = roles.stream()
                    .filter(r -> r.type() == type)
                    .toList();
            if (ofType.isEmpty()) continue;
            if (type != RoleType.TOWNSFOLK) {
                addEntry(new RoleTypeEntry(type, cardAreaWidth, cardHeight));
            }
            for (int i = 0; i < ofType.size(); i += 2) {
                ClocktowerRole left  = ofType.get(i);
                ClocktowerRole right = (i + 1 < ofType.size()) ? ofType.get(i + 1) : null;
                addEntry(new CardRowEntry(left, right, cardHeight, assetCache));
            }
        }
    }



    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_DOWN) {
            setScrollAmount(scrollAmount() + 20);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_UP) {
            setScrollAmount(scrollAmount() - 20);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public int getRowWidth() {
        return cardAreaWidth;
    }

    @Override
    public int getRowLeft() {
        return getX() + AbstractScrollArea.SCROLLBAR_WIDTH / 2;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener listener) { }

    @Override
    protected boolean scrollbarVisible() {
        return false;
    }

    public abstract static class Entry extends ObjectSelectionList.Entry<Entry> {
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return false;
        }
    }
}
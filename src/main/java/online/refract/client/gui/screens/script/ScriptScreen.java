package online.refract.client.gui.screens.script;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import online.refract.client.ClientAssetCache;
import online.refract.client.ClientCoordinator;
import online.refract.game.state.ClocktowerRole;
import online.refract.game.state.Enums.TownConnectionStatus;
import java.util.List;

public class ScriptScreen extends Screen {

    private static final float LIST_WIDTH_FRACTION = 2f / 3f;

    private final String editionName;
    private final List<ClocktowerRole> roles;
    private final ClientAssetCache assetCache;
    private final boolean doesScriptExist;

    private RoleListWidget listWidget;

    public ScriptScreen(ClientCoordinator coordinator) {
        super(Component.literal(coordinator.getState().scriptEdition()));
        this.editionName     = coordinator.getState().scriptEdition();
        this.roles           = coordinator.getState().roles();
        this.assetCache      = coordinator.getAssetCache();
        this.doesScriptExist = coordinator.getState().townConnectionStatus().equals(TownConnectionStatus.CONNECTED);
    }

    @Override
    protected void init() {
        if (!doesScriptExist) return;
        Font font = minecraft.font;

        int cardAreaWidth = (int) (width * LIST_WIDTH_FRACTION);
        int listWidth     = cardAreaWidth + AbstractScrollArea.SCROLLBAR_WIDTH;
        int listX         = (width - listWidth) / 2;

        int cardHeight = roles.stream().mapToInt(role -> CardRowEntry.computeCardHeight(role, cardAreaWidth, font)).max().orElse(20);

        listWidget = new RoleListWidget(minecraft, listWidth, height, 0, cardHeight, cardAreaWidth);
        listWidget.populate(editionName, roles, assetCache);
        listWidget.setPosition(listX, 0);
        addRenderableWidget(listWidget);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
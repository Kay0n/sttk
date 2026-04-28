package online.refract.client.gui.screens.script;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import online.refract.client.ClientAssetCache;
import online.refract.client.ClientCoordinator;
import online.refract.game.state.ClocktowerRole;
import online.refract.game.state.Enums.TownConnectionStatus;
import java.util.List;

public class ScriptScreen extends Screen {

    private static final float LIST_WIDTH_FRACTION = 2f / 3f;
    private static final Component ASTERIX_TEXT = Component.literal("* Not the first night");
    private static final float TEXT_SCALE = 0.75f;

    private final String editionName;
    private final List<ClocktowerRole> roles;
    private final ClientAssetCache assetCache;
    private final boolean isTownConnected;
    private RoleListWidget listWidget;



    public ScriptScreen(ClientCoordinator coordinator) {
        super(Component.literal(coordinator.getState().scriptEdition()));
        this.editionName     = coordinator.getState().scriptEdition();
        this.roles           = coordinator.getState().roles();
        this.assetCache      = coordinator.getAssetCache();
        this.isTownConnected = coordinator.getState().townConnectionStatus().equals(TownConnectionStatus.CONNECTED);
    }


    @Override
    protected void init() {
        if (!isTownConnected) return;

        int cardAreaWidth = (int) (width * LIST_WIDTH_FRACTION);
        int listWidth     = cardAreaWidth + AbstractScrollArea.SCROLLBAR_WIDTH;
        int listX         = (width - listWidth) / 2;
        int cardHeight = roles.stream().mapToInt(role -> CardRowEntry.computeCardHeight(role, cardAreaWidth, minecraft.font)).max().orElse(20);

        listWidget = new RoleListWidget(minecraft, listWidth, height, 0, cardHeight, cardAreaWidth);
        listWidget.populate(editionName, roles, assetCache);
        listWidget.setPosition(listX, 0);
        addRenderableWidget(listWidget);

        addButton("Night Order", 2, height - 22, () -> {

        });
    }


    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        drawAsterixText(graphics);
    }


    public void drawAsterixText(GuiGraphics graphics){
        int x = width - (int)(font.width(ASTERIX_TEXT) * TEXT_SCALE) - 3;
        int y = height - (int)(font.lineHeight * TEXT_SCALE) - 2;
        graphics.pose().pushMatrix();
        graphics.pose().translate(x, y);
        graphics.pose().scale(TEXT_SCALE, TEXT_SCALE);
        graphics.drawString(font, ASTERIX_TEXT, 0, 0, 0xFFFFFFFF);
        graphics.pose().popMatrix();
    }


    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    private void addButton(String label, int x, int y, Runnable action) {
        addRenderableWidget(
            Button.builder(Component.nullToEmpty(label), b -> action.run())
                .bounds(x, y, 78, 20)
                .build()
        );
    }
}
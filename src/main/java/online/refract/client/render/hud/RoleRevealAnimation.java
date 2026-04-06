package online.refract.client.render.hud;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.Util;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import online.refract.Sttk;
import online.refract.client.ClientAssetCache;

public class RoleRevealAnimation {

    private static final ResourceLocation TOKEN_TEXTURE = ResourceLocation.fromNamespaceAndPath("sttk", "textures/gui/token-greyscale.png");


    private static final int TOKEN_SIZE = 64;
    private static final float ICON_SCALE = 0.90f;
    private static final int GAP = 6;

    private static final int BANNER_TOP_OFFSET = 80;
    private static final int BANNER_BOTTOM_OFFSET = 50;

    private static final long FADE_IN_MS         = 750;
    private static final long HOLD_PREFIX_MS     = 1500;
    private static final long FADE_OUT_PREFIX_MS = 400;
    private static final long FADE_IN_ROLE_MS    = 600;
    private static final long HOLD_FULL_MS       = 4000;
    private static final long FADE_OUT_MS        = 750;

    private enum Phase {
        HIDDEN,
        FADE_IN_PREFIX,
        HOLD_PREFIX,
        FADE_OUT_PREFIX,
        FADE_IN_ROLE,
        HOLD_FULL,
        FADE_OUT,
        DONE
    }

    private static ClientAssetCache assetCache;
    private static final AnimationState animation = new AnimationState();

    private static String roleName = "";
    private static String roleAssetId = "";



    public static void register(ClientAssetCache cache) {
        assetCache = cache;
        HudElementRegistry.attachElementBefore(
            VanillaHudElements.CHAT,
            ResourceLocation.fromNamespaceAndPath(Sttk.MOD_ID, "role_reveal_animation"),
            RoleRevealAnimation::render
        );
    }


    public static void play(String role, String assetString) {
        roleName = role;
        roleAssetId = assetString;
        animation.start();
    }


    private static void render(GuiGraphics ctx, DeltaTracker delta) {
        if (!animation.isActive()) return;

        Minecraft client = Minecraft.getInstance();
        if (client.options.hideGui) return;

        float progress = animation.updateAndGetProgress();
        Phase phase = animation.phase;

        float prefixAlpha = getPrefixAlpha(phase, progress);
        float roleAlpha   = getRoleAlpha(phase, progress);
        float bgAlpha     = getBgAlpha(phase, progress);

        Layout layout = Layout.compute(ctx);

        drawBackground(ctx, layout, bgAlpha);
        drawYouAre(ctx, client, layout, prefixAlpha);
        drawRole(ctx, client, layout, roleAlpha);
    }


    private static float getPrefixAlpha(Phase phase, float t) {
        return switch (phase) {
            case FADE_IN_PREFIX  -> t;
            case HOLD_PREFIX     -> 1f;
            case FADE_OUT_PREFIX -> 1f - t;
            default -> 0f;
        };
    }


    private static float getRoleAlpha(Phase phase, float t) {
        return switch (phase) {
            case FADE_IN_ROLE -> t;
            case HOLD_FULL    -> 1f;
            case FADE_OUT     -> 1f - t;
            default -> 0f;
        };
    }


    private static float getBgAlpha(Phase phase, float t) {
        return switch (phase) {
            case FADE_IN_PREFIX -> t;
            case HOLD_PREFIX, FADE_OUT_PREFIX, FADE_IN_ROLE, HOLD_FULL -> 1f;
            case FADE_OUT -> 1f - t;
            default -> 0f;
        };
    }


    private static void drawBackground(GuiGraphics ctx, Layout layout, float alpha) {
        int a = (int)(alpha * 110);
        ctx.fill(0, layout.bannerTop, ctx.guiWidth(), layout.bannerBottom,
            ARGB.color(a, 0, 0, 0));
    }


    private static void drawYouAre(GuiGraphics ctx, Minecraft client, Layout layout, float alpha) {
        if (alpha <= 0) return;

        String text = "You are";
        int width = client.font.width(text) * 2;
        int height = client.font.lineHeight * 2;

        ctx.pose().pushMatrix();
        ctx.pose().translate(layout.centerX - width / 2f, layout.bannerCenterY - height / 2f);
        ctx.pose().scale(2f, 2f);

        ctx.drawString(client.font, text, 0, 0,
            ARGB.color((int)(alpha * 255), 255, 255, 255), true);

        ctx.pose().popMatrix();
    }


    private static void drawRole(GuiGraphics ctx, Minecraft client, Layout layout, float alpha) {
        if (alpha <= 0) return;

        int a = (int)(alpha * 255);

        drawToken(ctx, layout, a);
        drawRoleIcon(ctx, layout, a);
        drawRoleName(ctx, client, layout);
    }


    private static void drawToken(GuiGraphics ctx, Layout layout, int alpha) {
        int x = layout.centerX - TOKEN_SIZE / 2;
        int y = layout.bannerCenterY - (TOKEN_SIZE + GAP + (Minecraft.getInstance().font.lineHeight * 2)) / 2;

        ctx.blit(
            net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED,
            TOKEN_TEXTURE,
            x, y, 0f, 0f,
            TOKEN_SIZE, TOKEN_SIZE, TOKEN_SIZE, TOKEN_SIZE,
            ARGB.color(alpha, 255, 255, 255)
        );
    }


    private static void drawRoleIcon(GuiGraphics ctx, Layout layout, int alpha) {
        ResourceLocation tex = assetCache.getTexture(roleAssetId);
        if (tex == null) return;

        int size = (int)(TOKEN_SIZE * ICON_SCALE);
        int x = layout.centerX - size / 2;
        int y = layout.bannerCenterY - (TOKEN_SIZE + GAP + (Minecraft.getInstance().font.lineHeight * 2)) / 2
                + (TOKEN_SIZE - size) / 2;

        ctx.blit(
            net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED,
            tex,
            x, y, 0f, 0f,
            size, size, size, size,
            ARGB.color(alpha, 255, 255, 255)
        );
    }


    private static void drawRoleName(GuiGraphics ctx, Minecraft client, Layout layout) {
        int width = client.font.width(roleName) * 2;

        int y = layout.bannerCenterY
                - (TOKEN_SIZE + GAP + (client.font.lineHeight * 2)) / 2
                + TOKEN_SIZE + GAP;

        ctx.pose().pushMatrix();
        ctx.pose().translate(layout.centerX - width / 2f, y);
        ctx.pose().scale(2f, 2f);

        ctx.drawString(client.font, roleName, 0, 0, 0xFFFFFFFF, true);

        ctx.pose().popMatrix();
    }


    private static long getPhaseDuration(Phase p) {
        return switch (p) {
            case FADE_IN_PREFIX   -> FADE_IN_MS;
            case HOLD_PREFIX      -> HOLD_PREFIX_MS;
            case FADE_OUT_PREFIX  -> FADE_OUT_PREFIX_MS;
            case FADE_IN_ROLE     -> FADE_IN_ROLE_MS;
            case HOLD_FULL        -> HOLD_FULL_MS;
            case FADE_OUT         -> FADE_OUT_MS;
            default -> 0;
        };
    }


    private static Phase nextPhase(Phase p) {
        return Phase.values()[p.ordinal() + 1];
    }


    private static class Layout {
        @SuppressWarnings("unused")
        final int centerX, centerY;
        final int bannerTop, bannerBottom, bannerCenterY;

        private Layout(int cx, int cy, int top, int bottom) {
            this.centerX = cx;
            this.centerY = cy;
            this.bannerTop = top;
            this.bannerBottom = bottom;
            this.bannerCenterY = (top + bottom) / 2;
        }

        static Layout compute(GuiGraphics ctx) {
            int w = ctx.guiWidth();
            int h = ctx.guiHeight();
            int cx = w / 2;
            int cy = h / 2;

            return new Layout(
                cx,
                cy,
                cy - BANNER_TOP_OFFSET,
                cy + BANNER_BOTTOM_OFFSET
            );
        }
    }


    private static class AnimationState {
        private Phase phase = Phase.HIDDEN;
        private long phaseStartMs = 0;

        void start() {
            phase = Phase.FADE_IN_PREFIX;
            phaseStartMs = Util.getMillis();
        }

        boolean isActive() {
            return phase != Phase.HIDDEN && phase != Phase.DONE;
        }

        float updateAndGetProgress() {
            long now = Util.getMillis();
            long elapsed = now - phaseStartMs;
            long duration = getPhaseDuration(phase);

            if (elapsed >= duration) {
                phase = nextPhase(phase);
                phaseStartMs = now;
                return 0f;
            }

            return duration > 0 ? (float) elapsed / duration : 1f;
        }
    }
}
package online.refract.client.render.hud;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.Util;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import online.refract.Sttk;


import net.minecraft.util.ARGB;

public class RoleRevealAnimation {

    private enum Phase {
        HIDDEN,
        FADE_IN_PREFIX,
        HOLD_PREFIX,
        FADE_IN_ROLE,
        HOLD_FULL,
        FADE_OUT,
        DONE
    }

    // Durations in milliseconds
    private static final long FADE_IN_MS    = 500;
    private static final long HOLD_PREFIX_MS = 1000;
    private static final long FADE_IN_ROLE_MS = 600;
    private static final long HOLD_FULL_MS  = 1500;
    private static final long FADE_OUT_MS   = 750;

    private static Phase phase = Phase.HIDDEN;
    private static long phaseStartMs = 0;
    private static String roleName = "";

    public static void register() {
        HudElementRegistry.attachElementBefore(
            VanillaHudElements.CHAT,
            ResourceLocation.fromNamespaceAndPath(Sttk.MOD_ID, "role_reveal_animation"),
            RoleRevealAnimation::render
        );
    }

    public static void play(String role) {
        roleName = role;
        phase = Phase.FADE_IN_PREFIX;
        phaseStartMs = Util.getMillis();
    }


    private static void render(GuiGraphics context, DeltaTracker deltaTracker) {
        if (phase == Phase.HIDDEN || phase == Phase.DONE) return;

        Minecraft client = Minecraft.getInstance();
        if (client.options.hideGui) return;

        long now = Util.getMillis();
        long elapsed = now - phaseStartMs;

        // Advance phase if the current one has expired
        long phaseDuration = getPhaseDuration(phase);
        if (elapsed >= phaseDuration) {
            phase = nextPhase(phase);
            if (phase == Phase.HIDDEN || phase == Phase.DONE) return;
            phaseStartMs = now;
            elapsed = 0;
        }

        float progress = phaseDuration > 0 ? (float) elapsed / phaseDuration : 1f;

        float prefixAlpha = switch (phase) {
            case FADE_IN_PREFIX -> progress;
            case HOLD_PREFIX, FADE_IN_ROLE, HOLD_FULL -> 1f;
            case FADE_OUT -> 1f - progress;
            default -> 0f;
        };

        float roleAlpha = switch (phase) {
            case FADE_IN_ROLE -> progress;
            case HOLD_FULL -> 1f;
            case FADE_OUT -> 1f - progress;
            default -> 0f;
        };

        int screenW = context.guiWidth();
        int screenH = context.guiHeight();
        int centerX = screenW / 2;
        int centerY = screenH / 2;

        // Subtle dark background
        int bgAlpha = (int)(prefixAlpha * 110);
        context.fill(0, centerY - 45, screenW, centerY + 50, ARGB.color(bgAlpha, 0, 0, 0));

        // "You are" prefix
        if (prefixAlpha > 0) {
            String prefix = "You are";
            int prefixWidth = client.font.width(prefix);
            context.drawString(
                client.font,
                prefix,
                centerX - prefixWidth / 2,
                centerY - 18,
                ARGB.color((int)(prefixAlpha * 255), 255, 255, 255),
                true
            );
        }

        // Role name at 2x scale
        if (roleAlpha > 0) {
            int roleColor = ARGB.color((int)(roleAlpha * 255), 255, 215, 0); // gold
            // int roleWidth = client.font.width(roleName) * 2;
            context.pose().pushMatrix();
            // context.pose().translate(centerX - roleWidth / 2f, centerY + 4, 0);
            context.pose().scale(2f, 2f);
            context.drawString(client.font, roleName, 0, 0, roleColor, true);
            context.pose().popMatrix();
        }
    }

    private static long getPhaseDuration(Phase p) {
        return switch (p) {
            case FADE_IN_PREFIX  -> FADE_IN_MS;
            case HOLD_PREFIX     -> HOLD_PREFIX_MS;
            case FADE_IN_ROLE    -> FADE_IN_ROLE_MS;
            case HOLD_FULL       -> HOLD_FULL_MS;
            case FADE_OUT        -> FADE_OUT_MS;
            default -> 0;
        };
    }

    private static Phase nextPhase(Phase p) {
        return Phase.values()[p.ordinal() + 1];
    }
}
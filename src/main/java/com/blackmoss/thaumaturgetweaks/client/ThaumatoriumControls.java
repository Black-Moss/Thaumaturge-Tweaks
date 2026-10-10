package com.blackmoss.thaumaturgetweaks.client;

import com.blackmoss.thaumaturgetweaks.ThaumaturgeTweaks;
import com.blackmoss.thaumaturgetweaks.mixin.client.screen.essentia.ThaumatoriumScreenAccessor;
import com.leclowndu93150.thaumaturge.client.screen.ThaumatoriumScreen;
import com.leclowndu93150.thaumaturge.content.essentia.thaumatorium.MenuThaumatorium;
import com.leclowndu93150.thaumaturge.registry.TTSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = ThaumaturgeTweaks.MODID, value = Dist.CLIENT)
public final class ThaumatoriumControls {
    private static final int GRID_X = 48;
    private static final int GRID_Y = 56;
    private static final int GRID_W = 32;
    private static final int GRID_H = 48;
    private static final int VISIBLE = 6;

    private ThaumatoriumControls() {
    }

    @SubscribeEvent
    public static void onMouseScrolled(ScreenEvent.MouseScrolled.Pre event) {
        if (!(event.getScreen() instanceof ThaumatoriumScreen screen)) {
            return;
        }
        int mouseX = (int) event.getMouseX();
        int mouseY = (int) event.getMouseY();
        int left = screen.getLeftPos();
        int top = screen.getTopPos();
        if (!inRect(mouseX, mouseY, left + GRID_X, top + GRID_Y, GRID_W, GRID_H)) {
            return;
        }
        double delta = event.getScrollDeltaY();
        int step;
        if (delta > 0.0) {
            step = -1;
        } else if (delta < 0.0) {
            step = 1;
        } else {
            return;
        }
        if (scroll(screen, step)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onKeyPressed(ScreenEvent.KeyPressed.Pre event) {
        if (!(event.getScreen() instanceof ThaumatoriumScreen screen)) {
            return;
        }
        int step;
        switch (event.getKeyCode()) {
            case GLFW.GLFW_KEY_PAGE_UP, GLFW.GLFW_KEY_UP -> step = -1;
            case GLFW.GLFW_KEY_PAGE_DOWN, GLFW.GLFW_KEY_DOWN -> step = 1;
            default -> {
                return;
            }
        }
        if (scroll(screen, step)) {
            event.setCanceled(true);
        }
    }

    private static boolean scroll(ThaumatoriumScreen screen, int step) {
        MenuThaumatorium menu = screen.getMenu();
        if (menu.clientRecipes == null) {
            return false;
        }
        int size = menu.clientRecipes.size();
        if (size <= VISIBLE) {
            return false;
        }
        ThaumatoriumScreenAccessor accessor = (ThaumatoriumScreenAccessor) (Object) screen;
        int index = accessor.thaumaturgetweaks$index();
        int next;
        if (step < 0) {
            if (index <= 0) {
                return false;
            }
            next = index - 1;
        } else {
            if ((float) index >= (float) size / 2.0F - 3.0F) {
                return false;
            }
            next = index + 1;
        }
        accessor.thaumaturgetweaks$setIndex(next);
        playScrollSound();
        return true;
    }

    private static boolean inRect(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    private static void playScrollSound() {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            player.playSound(TTSounds.KEY.get(), 0.3F, 1.0F);
        }
    }
}

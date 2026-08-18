// 神秘炼金塔界面（ThaumatoriumScreen）操作增强：
// - 鼠标位于配方网格区域时：滚轮上滚 = 上一页，下滚 = 下一页（与点上下箭头等效）
// - PageUp / 上方向键 = 上一页，PageDown / 下方向键 = 下一页
// 通过 Mixin accessor（ThaumatoriumScreenAccessor）读写私有 index 字段，避免反射。
package com.blackmoss.thaumaturgetweaks.client;

import com.blackmoss.thaumaturgetweaks.ThaumaturgeTweaks;
import com.blackmoss.thaumaturgetweaks.mixin.client.screen.essentia.ThaumatoriumScreenAccessor;
import com.leclowndu93150.thaumaturge.client.screen.ThaumatoriumScreen;
import com.leclowndu93150.thaumaturge.content.essentia.thaumatorium.MenuThaumatorium;
import com.leclowndu93150.thaumaturge.registry.TCSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = ThaumaturgeTweaks.MODID, value = Dist.CLIENT)
public final class ThaumatoriumControls {

    // 配方网格区域（相对界面左上角），与 ThaumatoriumScreen 常量一致。
    private static final int GRID_X = 48;
    private static final int GRID_Y = 56;
    private static final int GRID_W = 32; // 2 列 x 16
    private static final int GRID_H = 48; // 3 行 x 16
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
        // 仅在鼠标位于配方网格区域时响应滚轮，避免影响背包等其它区域。
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

    // 配方列表翻页：步进 -1=上一页 1=下一页，边界与界面上下箭头的可见条件一致。
    private static boolean scroll(ThaumatoriumScreen screen, int step) {
        MenuThaumatorium menu = screen.getMenu();
        if (menu.clientRecipes == null) {
            return false;
        }
        int size = menu.clientRecipes.size();
        // 配方不超过一页时无需翻页。
        if (size <= VISIBLE) {
            return false;
        }
        ThaumatoriumScreenAccessor accessor = (ThaumatoriumScreenAccessor) (Object) screen;
        int index = accessor.thaumaturgetweaks$index();
        int next;
        if (step < 0) {
            // 上一页：与向上箭头条件一致（index > 0）。
            if (index <= 0) {
                return false;
            }
            next = index - 1;
        } else {
            // 下一页：与向下箭头条件一致（index < size/2 - 3）。
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
            player.playSound(TCSounds.KEY.get(), 0.3F, 1.0F);
        }
    }
}

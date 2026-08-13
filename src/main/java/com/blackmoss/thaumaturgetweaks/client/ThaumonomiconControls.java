package com.blackmoss.thaumaturgetweaks.client;

import com.blackmoss.thaumaturgetweaks.mixin.client.screen.research.EntryDetailScreenAccessor;
import com.leclowndu93150.thaumaturge.client.screen.research.EntryDetailScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = com.blackmoss.thaumaturgetweaks.ThaumaturgeTweaks.MODID, value = Dist.CLIENT)
public final class ThaumonomiconControls {

    private ThaumonomiconControls() {
    }

    @SubscribeEvent
    public static void onKeyPressed(ScreenEvent.KeyPressed.Pre event) {
        if (!(event.getScreen() instanceof EntryDetailScreen screen)) {
            return;
        }
        int key = event.getKeyCode();
        if (key == GLFW.GLFW_KEY_BACKSPACE) {
            screen.onClose();
            event.setCanceled(true);
        } else if (key == GLFW.GLFW_KEY_LEFT && !inSubView(screen)) {
            accessor(screen).thaumaturgetweaks$prevPage();
            event.setCanceled(true);
        } else if (key == GLFW.GLFW_KEY_RIGHT && !inSubView(screen)) {
            accessor(screen).thaumaturgetweaks$nextPage();
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onMouseButton(ScreenEvent.MouseButtonPressed.Pre event) {
        if (!(event.getScreen() instanceof EntryDetailScreen screen)) {
            return;
        }
        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            screen.onClose();
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onMouseScrolled(ScreenEvent.MouseScrolled.Pre event) {
        if (!(event.getScreen() instanceof EntryDetailScreen screen)) {
            return;
        }
        if (inSubView(screen)) {
            return;
        }
        double delta = event.getScrollDeltaY();
        if (delta < 0.0) {
            accessor(screen).thaumaturgetweaks$nextPage();
        } else if (delta > 0.0) {
            accessor(screen).thaumaturgetweaks$prevPage();
        }
        event.setCanceled(true);
    }

    // 处于配方/要素/知识子视图时，翻页键不作用于主页面。
    private static boolean inSubView(EntryDetailScreen screen) {
        EntryDetailScreenAccessor accessor = accessor(screen);
        return accessor.thaumaturgetweaks$shownRecipe() != null
                || accessor.thaumaturgetweaks$showingAspects()
                || accessor.thaumaturgetweaks$showingKnowledge();
    }

    // Mixin 在运行时为 EntryDetailScreen 注入该接口，因此先经 Object 转换以通过编译期检查。
    private static EntryDetailScreenAccessor accessor(EntryDetailScreen screen) {
        return (EntryDetailScreenAccessor) (Object) screen;
    }
}

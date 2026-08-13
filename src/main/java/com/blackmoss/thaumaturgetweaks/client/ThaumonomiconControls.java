// 魔导手册研究页（EntryDetailScreen）增强操作：
// - 滚轮上下 = 翻页
// - 退格 / 鼠标右键 = 关闭研究页（子视图时先返回主视图）
// - 左 / 右方向键 = 翻页
// 由于 EntryDetailScreen 的 nextPage/prevPage 为 private，且 NeoForge 使用 official mappings
// 运行时方法名稳定，这里通过反射调用；找不到时仅记录警告并降级为不响应。
package com.blackmoss.thaumaturgetweaks.client;

import com.blackmoss.thaumaturgetweaks.ThaumaturgeTweaks;
import com.leclowndu93150.thaumaturge.client.screen.research.EntryDetailScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@EventBusSubscriber(modid = ThaumaturgeTweaks.MODID, value = Dist.CLIENT)
public final class ThaumonomiconControls {

    // private 翻页方法与子视图标记字段（official mappings 下名称稳定）。
    private static final Method NEXT_PAGE = findMethod("nextPage");
    private static final Method PREV_PAGE = findMethod("prevPage");
    private static final Field SHOWN_RECIPE = findField("shownRecipe");
    private static final Field SHOWING_ASPECTS = findField("showingAspects");
    private static final Field SHOWING_KNOWLEDGE = findField("showingKnowledge");
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
            invoke(PREV_PAGE, screen);
            event.setCanceled(true);
        } else if (key == GLFW.GLFW_KEY_RIGHT && !inSubView(screen)) {
            invoke(NEXT_PAGE, screen);
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
            invoke(NEXT_PAGE, screen);
        } else if (delta > 0.0) {
            invoke(PREV_PAGE, screen);
        }
        event.setCanceled(true);
    }

    // 处于配方/要素/知识子视图时，翻页键不作用于主页面。
    private static boolean inSubView(EntryDetailScreen screen) {
        try {
            if (SHOWN_RECIPE != null && SHOWN_RECIPE.get(screen) != null) {
                return true;
            }
            return (SHOWING_ASPECTS != null && Boolean.TRUE.equals(SHOWING_ASPECTS.get(screen)))
                    || (SHOWING_KNOWLEDGE != null && Boolean.TRUE.equals(SHOWING_KNOWLEDGE.get(screen)));
        } catch (IllegalAccessException e) {
            return false;
        }
    }

    private static void invoke(Method method, EntryDetailScreen screen) {
        if (method == null) {
            return;
        }
        try {
            method.invoke(screen);
        } catch (ReflectiveOperationException e) {
            ThaumaturgeTweaks.LOGGER.warn("ThaumaturgeTweaks: failed to invoke {} on EntryDetailScreen", method.getName(), e);
        }
    }

    private static Method findMethod(String name) {
        try {
            Method method = EntryDetailScreen.class.getDeclaredMethod(name);
            method.setAccessible(true);
            return method;
        } catch (ReflectiveOperationException e) {
            ThaumaturgeTweaks.LOGGER.warn("ThaumaturgeTweaks: missing method {} on EntryDetailScreen", name);
            return null;
        }
    }

    private static Field findField(String name) {
        try {
            Field field = EntryDetailScreen.class.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (ReflectiveOperationException e) {
            ThaumaturgeTweaks.LOGGER.warn("ThaumaturgeTweaks: missing field {} on EntryDetailScreen", name);
            return null;
        }
    }
}

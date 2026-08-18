package com.blackmoss.thaumaturgetweaks.client;

import com.leclowndu93150.thaumaturge.api.aspect.AspectIndexAccess;
import com.leclowndu93150.thaumaturge.api.aspect.AspectInstance;
import com.leclowndu93150.thaumaturge.api.aspect.AspectList;
import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

// 安瓿/水晶碎片在按住 Shift 时显示为对应要素图标（含要素颜色），松开恢复默认纹理。
// 渲染替换由 ItemModelResolverMixin + AspectIconSpecialRenderer 实现，
// 本类仅负责提取目标物品（要素安瓿、要素水晶碎片）的要素。
public final class AspectSlotAnnotations {

    // 需要替换渲染的目标物品（注册 ID）。
    private static final Identifier PHIAL_ID = Identifier.fromNamespaceAndPath("thaumaturge", "phial");
    private static final Identifier ESSENTIA_CRYSTAL_ID =
            Identifier.fromNamespaceAndPath("thaumaturge", "essentia_crystal");

    private AspectSlotAnnotations() {}

    // 若 stack 是要素安瓿或要素水晶碎片，返回其主要要素；否则返回 null。
    public static Holder<IAspect> aspectOf(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if ((!id.equals(PHIAL_ID) && !id.equals(ESSENTIA_CRYSTAL_ID))) {
            return null;
        }
        AspectList aspects = AspectIndexAccess.of(stack);
        if (aspects == null || aspects.isEmpty()) {
            return null;
        }
        AspectInstance primary = aspects.entries().getFirst();
        if (primary == null || primary.aspect() == null) {
            return null;
        }
        return primary.aspect();
    }
}

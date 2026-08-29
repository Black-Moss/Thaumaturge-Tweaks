// 客户端：为魔导透镜补充容器方块扫描的用法提示。
// 物品栏扫描已由本体 Thaumaturge 0.2.0 内置的 InventoryScanHandler 提供（含其自带提示），
// 因此这里只提示本模组独有的容器方块扫描，避免与本体提示重复。
package com.blackmoss.thaumaturgetweaks.containerscan;

import com.blackmoss.thaumaturgetweaks.ThaumaturgeTweaks;
import com.leclowndu93150.thaumaturge.registry.TCItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = ThaumaturgeTweaks.MODID, value = Dist.CLIENT)
public final class ContainerScanTooltip {

    private ContainerScanTooltip() {
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (!event.getItemStack().is(TCItems.THAUMOMETER.get())) {
            return;
        }
        event.getToolTip()
                .add(Component.translatable("thaumaturgetweaks.containerscan.tooltip")
                        .withStyle(ChatFormatting.DARK_AQUA));
    }
}

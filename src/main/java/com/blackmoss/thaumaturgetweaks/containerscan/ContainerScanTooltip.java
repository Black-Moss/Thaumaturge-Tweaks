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

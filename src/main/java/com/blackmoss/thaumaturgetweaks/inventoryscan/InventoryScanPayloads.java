// 物品栏扫描网络包的服务端/客户端处理器。
package com.blackmoss.thaumaturgetweaks.inventoryscan;

import com.blackmoss.thaumaturgetweaks.inventoryscan.client.InventoryScanningClient;
import com.leclowndu93150.thaumaturge.api.research.scan.ScanningManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class InventoryScanPayloads {
    private InventoryScanPayloads() {
    }

    // 服务器：扫描玩家打开容器中的指定槽位物品。
    static void handleScanSlot(ServerboundScanSlotPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            AbstractContainerMenu menu = player.containerMenu;
            int slotNumber = payload.slotNumber();
            if (slotNumber < 0 || slotNumber >= menu.slots.size()) {
                return;
            }
            Slot slot = menu.slots.get(slotNumber);
            if (slot instanceof ResultSlot || !slot.mayPickup(player)) {
                return;
            }
            ItemStack stack = slot.getItem();
            if (stack.isEmpty() || !ScanningManager.isThingStillScannable(player, stack)) {
                return;
            }
            ScanningManager.scanTheThing(player, stack);
        });
    }

    // 服务器：扫描玩家自身。
    static void handleScanSelf(ServerboundScanSelfPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            if (!ScanningManager.isThingStillScannable(player, player)) {
                return;
            }
            ScanningManager.scanTheThing(player, player);
        });
    }

    // 客户端：服务器已安装本模组，允许发送扫描请求。
    static void handleScanAvailable(ClientboundScanAvailablePayload payload, IPayloadContext context) {
        context.enqueueWork(InventoryScanningClient::markServerAvailable);
    }
}

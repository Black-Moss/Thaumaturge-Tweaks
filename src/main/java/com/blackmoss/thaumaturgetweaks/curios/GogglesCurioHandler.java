// 揭示之护目镜优先放入饰品栏：右键使用护目镜时，若饰品栏 head 槽有空位则优先放入，
// 而不是默认装备到头盔槽（装备栏）。head 槽无空位时回退原版行为。
//
// 双端协调：客户端先判断 head 槽镜像是否有空位，有空位则取消原版装备（阻止 Equippable
// 客户端预测产生的幽灵物品），并发送请求包；服务端收到后做权威放入，结果经 Curios
// 槽数据同步回客户端。无空位时双端都不拦截，走原版装备到头盔槽。
//
// 需要 Curios 模组；由主类在 ModList 确认 Curios 已加载后才调用 register()，
// 避免在 Curios 缺失时因引用其类而抛 NoClassDefFoundError。
package com.blackmoss.thaumaturgetweaks.curios;

import com.leclowndu93150.thaumaturge.registry.TCItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Optional;

public final class GogglesCurioHandler {

    private GogglesCurioHandler() {
    }

    // 仅当 Curios 已加载时由主类调用。
    public static void register(IEventBus modBus) {
        // 右键拦截（双端都监听：客户端阻止预测，服务端兜底）。
        NeoForge.EVENT_BUS.addListener(GogglesCurioHandler::onRightClickItem);
        // 请求包处理（服务端）。
        modBus.addListener(GogglesCurioHandler::registerPayloads);
    }

    private static void registerPayloads(net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(
                ServerboundEquipGogglesPayload.TYPE,
                ServerboundEquipGogglesPayload.STREAM_CODEC,
                GogglesCurioHandler::handleEquipGoggles);
    }

    // 右键使用护目镜：
    // - 客户端：head 槽镜像有空位 -> 取消原版装备并发送请求包；无空位则放行原版。
    // - 服务端：双保险，收到 use 包时同样拦截（正常情况下客户端已拦截，此处兜底）。
    private static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack held = player.getItemInHand(event.getHand());
        if (!held.is(TCItems.GOGGLES_REVEALING.get())) {
            return;
        }
        boolean client = player.level().isClientSide();
        // 检查 head 槽是否有空位（客户端用镜像，服务端用真实数据）。
        if (hasEmptyHeadSlot(player)) {
            if (client) {
                // 阻止 Equippable 客户端预测装备，并请求服务端放入。
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
                ClientPacketDistributor.sendToServer(ServerboundEquipGogglesPayload.INSTANCE);
            } else {
                // 服务端兜底（理论上客户端已拦截，此处仅处理客户端漏发的场景）。
                if (equipToHeadCurio(player, held)) {
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                }
            }
        }
        // 无空位：双端放行，走原版装备到头盔槽。
    }

    // 服务端处理请求包：将手持护目镜放入饰品栏 head 槽。
    private static void handleEquipGoggles(ServerboundEquipGogglesPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            ItemStack held = player.getMainHandItem();
            if (!held.is(TCItems.GOGGLES_REVEALING.get())) {
                return;
            }
            equipToHeadCurio(player, held);
        });
    }

    // 服务端：将护目镜放入饰品栏 head 槽。成功时消耗手中物品并返回 true。
    private static boolean equipToHeadCurio(Player player, ItemStack held) {
        Optional<ICuriosItemHandler> invOpt = CuriosApi.getCuriosInventory(player);
        if (invOpt.isEmpty()) {
            return false;
        }
        ICurioStacksHandler head = invOpt.get().getCurios().get("head");
        if (head == null) {
            return false;
        }
        var stacks = head.getStacks();
        for (int slot = 0; slot < stacks.getSlots(); slot++) {
            if (!stacks.getStackInSlot(slot).isEmpty()) {
                continue;
            }
            // 直接放入空槽（IItemHandlerModifiable.setStackInSlot 未废弃；insertItem 已标记待移除）。
            stacks.setStackInSlot(slot, held.copy());
            held.shrink(1);
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.playSound(SoundEvents.ARMOR_EQUIP_LEATHER.value(), 1.0F, 1.0F);
            }
            return true;
        }
        return false;
    }

    // 检查饰品栏 head 槽是否有空位（客户端/服务端均可读取当前槽位状态）。
    private static boolean hasEmptyHeadSlot(Player player) {
        Optional<ICuriosItemHandler> invOpt = CuriosApi.getCuriosInventory(player);
        if (invOpt.isEmpty()) {
            return false;
        }
        ICurioStacksHandler head = invOpt.get().getCurios().get("head");
        if (head == null) {
            return false;
        }
        var stacks = head.getStacks();
        for (int slot = 0; slot < stacks.getSlots(); slot++) {
            if (stacks.getStackInSlot(slot).isEmpty()) {
                return true;
            }
        }
        return false;
    }
}

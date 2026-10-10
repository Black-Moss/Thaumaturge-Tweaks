package com.blackmoss.thaumaturgetweaks.compat.curios;

import com.leclowndu93150.thaumaturge.registry.TTItems;
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

    public static void register(IEventBus modBus) {
        NeoForge.EVENT_BUS.addListener(GogglesCurioHandler::onRightClickItem);
        modBus.addListener(GogglesCurioHandler::registerPayloads);
    }

    private static void registerPayloads(net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(
                ServerboundEquipGogglesPayload.TYPE,
                ServerboundEquipGogglesPayload.STREAM_CODEC,
                GogglesCurioHandler::handleEquipGoggles);
    }

    private static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack held = player.getItemInHand(event.getHand());
        if (!held.is(TTItems.GOGGLES_REVEALING.get())) {
            return;
        }
        boolean client = player.level().isClientSide();
        if (hasEmptyHeadSlot(player)) {
            if (client) {
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
                ClientPacketDistributor.sendToServer(ServerboundEquipGogglesPayload.INSTANCE);
            } else {
                if (equipToHeadCurio(player, held)) {
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                }
            }
        }
    }

    private static void handleEquipGoggles(ServerboundEquipGogglesPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            ItemStack held = player.getMainHandItem();
            if (!held.is(TTItems.GOGGLES_REVEALING.get())) {
                return;
            }
            equipToHeadCurio(player, held);
        });
    }

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
            stacks.setStackInSlot(slot, held.copy());
            held.shrink(1);
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.playSound(SoundEvents.ARMOR_EQUIP_LEATHER.value(), 1.0F, 1.0F);
            }
            return true;
        }
        return false;
    }

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

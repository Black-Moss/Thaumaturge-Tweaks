package com.blackmoss.thaumaturgetweaks.compat.trinkets;

import com.leclowndu93150.thaumaturge.api.items.GogglesAccess;
import com.leclowndu93150.thaumaturge.registry.TCItems;
import eu.pb4.trinkets.api.TrinketInventory;
import eu.pb4.trinkets.api.TrinketSlotAccess;
import eu.pb4.trinkets.api.TrinketSlotUtils;
import eu.pb4.trinkets.api.TrinketsApi;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jspecify.annotations.Nullable;

import java.util.Map;


public final class GogglesTrinketHandler {
    private static final String HEAD_SLOT_PREFIX = "head/";

    private GogglesTrinketHandler() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(GogglesTrinketHandler::onRegisterPayloads);
        NeoForge.EVENT_BUS.addListener(GogglesTrinketHandler::onRightClickItem);
    }

    public static boolean wearsGoggles(LivingEntity entity) {
        if (entity == null) {
            return false;
        }
        return TrinketsApi.getAttachment(entity)
                .findFirst(GogglesAccess::isRevealing)
                .isPresent();
    }

    private static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(
                ServerboundEquipTrinketsGogglesPayload.TYPE,
                ServerboundEquipTrinketsGogglesPayload.STREAM_CODEC,
                GogglesTrinketHandler::handleEquipGoggles);
    }

    private static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.isCanceled()) {
            return;
        }
        Player player = event.getEntity();
        ItemStack held = player.getItemInHand(event.getHand());
        if (!held.is(TCItems.GOGGLES_REVEALING.get())) {
            return;
        }
        if (player.level().isClientSide()) {
            if (!hasEmptyHeadSlot(player)) {
                return;
            }
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            ClientPacketDistributor.sendToServer(ServerboundEquipTrinketsGogglesPayload.INSTANCE);
        } else if (equipToHeadTrinket(player, held)) {
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }

    private static void handleEquipGoggles(ServerboundEquipTrinketsGogglesPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            ItemStack held = player.getMainHandItem();
            if (held.is(TCItems.GOGGLES_REVEALING.get())) {
                equipToHeadTrinket(player, held);
            }
        });
    }

    private static @Nullable TrinketSlotAccess findFreeHeadSlot(Player player, ItemStack held) {
        Map<String, TrinketInventory> inventories = TrinketsApi.getAttachment(player).getInventories();
        for (Map.Entry<String, TrinketInventory> entry : inventories.entrySet()) {
            if (!entry.getKey().startsWith(HEAD_SLOT_PREFIX)) {
                continue;
            }
            TrinketInventory inventory = entry.getValue();
            for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
                if (!inventory.getItem(slot).isEmpty()) {
                    continue;
                }
                TrinketSlotAccess access = inventory.getSlotAccess(slot);
                if (access == null || !access.isValid() || !TrinketSlotUtils.mayPlace(access, held)) {
                    continue;
                }
                return access;
            }
        }
        return null;
    }

    private static boolean hasEmptyHeadSlot(Player player) {
        return findFreeHeadSlot(player, player.getMainHandItem()) != null;
    }

    private static boolean equipToHeadTrinket(Player player, ItemStack held) {
        TrinketSlotAccess slot = findFreeHeadSlot(player, held);
        if (slot == null || !slot.set(held.copy())) {
            return false;
        }
        held.shrink(1);
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.playSound(SoundEvents.ARMOR_EQUIP_LEATHER.value(), 1.0F, 1.0F);
        }
        return true;
    }
}

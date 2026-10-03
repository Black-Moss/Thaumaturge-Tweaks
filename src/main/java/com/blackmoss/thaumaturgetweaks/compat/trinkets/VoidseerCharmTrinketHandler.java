package com.blackmoss.thaumaturgetweaks.compat.trinkets;

import com.leclowndu93150.thaumaturge.content.equipment.bauble.VoidseerCharmItem;
import com.leclowndu93150.thaumaturge.registry.TCItems;
import eu.pb4.trinkets.api.TrinketSlotAccess;
import eu.pb4.trinkets.api.callback.TrinketCallback;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

public final class VoidseerCharmTrinketHandler {
    private VoidseerCharmTrinketHandler() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(VoidseerCharmTrinketHandler::onCommonSetup);
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> TrinketCallback.setCallback(TCItems.VOIDSEER_CHARM.get(), new Callback()));
    }

    private static final class Callback implements TrinketCallback {

        @Override
        public void tick(ItemStack stack, TrinketSlotAccess slot, LivingEntity entity) {
            if (stack.getItem() instanceof VoidseerCharmItem charm) {
                charm.wornTick(stack, entity);
            }
        }

        @Override
        public void onUnequip(ItemStack stack, TrinketSlotAccess slot, LivingEntity entity) {
            if (stack.getItem() instanceof VoidseerCharmItem) {
                VoidseerCharmItem.clearDiscount(entity);
            }
        }
    }
} 

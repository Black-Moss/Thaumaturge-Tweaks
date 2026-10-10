package com.blackmoss.thaumaturgetweaks.compat.trinkets;

import com.leclowndu93150.thaumaturge.content.equipment.bauble.AmuletVisItem;
import com.leclowndu93150.thaumaturge.registry.TTItems;
import eu.pb4.trinkets.api.TrinketSlotAccess;
import eu.pb4.trinkets.api.callback.TrinketCallback;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

public final class AmuletVisTrinketHandler {

    private AmuletVisTrinketHandler() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(AmuletVisTrinketHandler::onCommonSetup);
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            TrinketCallback.setCallback(TTItems.AMULET_VIS.get(), new Callback());
            TrinketCallback.setCallback(TTItems.AMULET_VIS_CRAFTED.get(), new Callback());
        });
    }

    private static final class Callback implements TrinketCallback {

        @Override
        public void tick(ItemStack stack, TrinketSlotAccess slot, LivingEntity entity) {
            if (stack.getItem() instanceof AmuletVisItem amulet) {
                amulet.wornTick(stack, entity);
            }
        }
    }
} 

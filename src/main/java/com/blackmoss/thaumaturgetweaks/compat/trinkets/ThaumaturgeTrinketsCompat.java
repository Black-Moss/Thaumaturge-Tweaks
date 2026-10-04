package com.blackmoss.thaumaturgetweaks.compat.trinkets;

import com.leclowndu93150.thaumaturge.api.items.IVisDiscountGear;
import com.leclowndu93150.thaumaturge.api.items.RechargeAccess;
import com.leclowndu93150.thaumaturge.content.equipment.bauble.AmuletVisItem;
import com.leclowndu93150.thaumaturge.content.equipment.bauble.VerdantCharmItem;
import com.leclowndu93150.thaumaturge.content.equipment.bauble.VoidseerCharmItem;
import com.leclowndu93150.thaumaturge.registry.TCAttributes;
import com.leclowndu93150.thaumaturge.registry.TCItems;
import eu.pb4.trinkets.api.TrinketSlotAccess;
import eu.pb4.trinkets.api.TrinketsApi;
import eu.pb4.trinkets.api.callback.TrinketCallback;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public final class ThaumaturgeTrinketsCompat {
    private static final boolean TRINKETS_LOADED = ModList.get().isLoaded("trinkets_updated");

    private ThaumaturgeTrinketsCompat() {
    }

    public static boolean isActive() {
        return TRINKETS_LOADED;
    }

    public static void register(IEventBus modBus) {
        GogglesTrinketHandler.register(modBus);
        CuriosityBandTrinketHandler.register(modBus);
        AmuletVisTrinketHandler.register(modBus);
        VerdantCharmTrinketHandler.register(modBus);
        VoidseerCharmTrinketHandler.register(modBus);
        modBus.addListener(ThaumaturgeTrinketsCompat::onCommonSetup);
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(ThaumaturgeTrinketsCompat::registerItemCallbacks);
    }

    private static void registerItemCallbacks() {
        for (var entry : TCItems.ITEMS.getEntries()) {
            Item item = entry.get();
            if (hasOwnHandler(item) || !(item instanceof IVisDiscountGear)) {
                continue;
            }
            TrinketCallback.setCallback(item, new VisDiscountCallback());
        }
    }

    private static boolean hasOwnHandler(Item item) {
        return item instanceof AmuletVisItem
                || item instanceof VerdantCharmItem
                || item instanceof VoidseerCharmItem;
    }

    public static boolean anyTrinketMatches(LivingEntity entity, Predicate<ItemStack> predicate) {
        if (entity == null || predicate == null) {
            return false;
        }
        return TrinketsApi.getAttachment(entity).isEquipped(predicate);
    }

    public static List<ItemStack> equippedTrinkets(LivingEntity entity) {
        if (entity == null) {
            return List.of();
        }
        List<ItemStack> stacks = new ArrayList<>();
        TrinketsApi.getAttachment(entity).forEach((slot, stack) -> {
            if (!stack.isEmpty()) {
                stacks.add(stack);
            }
        });
        return stacks;
    }

    public static boolean isEquipped(LivingEntity entity, Item item) {
        if (entity == null || item == null) {
            return false;
        }
        return anyTrinketMatches(entity, stack -> stack.is(item));
    }

    public static ItemStack extractTrinket(LivingEntity entity, Item item) {
        if (entity == null || item == null) {
            return ItemStack.EMPTY;
        }
        Optional<TrinketSlotAccess> found = TrinketsApi.getAttachment(entity).findFirst(stack -> stack.is(item));
        if (found.isEmpty()) {
            return ItemStack.EMPTY;
        }
        TrinketSlotAccess slot = found.get();
        ItemStack removed = slot.get().copy();
        slot.set(ItemStack.EMPTY);
        return removed;
    }

    public static boolean rechargeFirstTrinket(Player player) {
        if (player == null) {
            return false;
        }
        for (ItemStack stack : equippedTrinkets(player)) {
            if (RechargeAccess.rechargeItem(player.level(), stack, player.blockPosition(), player, 1) > 0.0F) {
                return true;
            }
        }
        return false;
    }

    private record VisDiscountCallback() implements TrinketCallback {

        @Override
        public void forEachTrinketModifier(
                ItemStack stack,
                TrinketSlotAccess slot,
                LivingEntity entity,
                Identifier slotIdentifier,
                BiConsumer<Holder<Attribute>, AttributeModifier> consumer) {
            if (!(stack.getItem() instanceof IVisDiscountGear gear)) {
                return;
            }
            float contribution = gear.getVisDiscount(stack) / 100.0F;
            if (contribution != 0.0F) {
                consumer.accept(
                        TCAttributes.VIS_DISCOUNT,
                        new AttributeModifier(
                                BuiltInRegistries.ITEM.getKey(stack.getItem()),
                                contribution,
                                AttributeModifier.Operation.ADD_VALUE));
            }
        }
    }
}

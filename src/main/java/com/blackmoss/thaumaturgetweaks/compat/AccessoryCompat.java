package com.blackmoss.thaumaturgetweaks.compat;

import com.blackmoss.thaumaturgetweaks.compat.trinkets.ThaumaturgeTrinketsCompat;
import com.blackmoss.thaumaturgetweaks.compat.trinkets.TrinketSlotAdapter;
import com.leclowndu93150.thaumaturge.TCIds;
import com.leclowndu93150.thaumaturge.compat.curio.ThaumaturgeCuriosCompat;
import com.leclowndu93150.thaumaturge.compat.curio.ThaumaturgeCuriosCompat.CurioPouchRef;
import eu.pb4.trinkets.api.TrinketSlotAccess;
import eu.pb4.trinkets.api.TrinketsApi;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jspecify.annotations.Nullable;

public final class AccessoryCompat {
    private AccessoryCompat() {
    }

    public static boolean isAnyAccessoryModLoaded() {
        return ModList.get().isLoaded(TCIds.CURIOS) || ThaumaturgeTrinketsCompat.isActive();
    }

    public static boolean isEquipped(LivingEntity entity, Item item) {
        if (entity == null || item == null) {
            return false;
        }
        if (ModList.get().isLoaded(TCIds.CURIOS) && ThaumaturgeCuriosCompat.isCurioEquipped(entity, item)) {
            return true;
        }
        return ThaumaturgeTrinketsCompat.isActive() && ThaumaturgeTrinketsCompat.isEquipped(entity, item);
    }

    public static ItemStack extractEquipped(LivingEntity entity, @Nullable Item item) {
        if (entity == null || item == null) {
            return ItemStack.EMPTY;
        }
        if (ModList.get().isLoaded(TCIds.CURIOS)) {
            ItemStack removed = ThaumaturgeCuriosCompat.extractCurio(entity, item);
            if (!removed.isEmpty()) {
                return removed;
            }
        }
        if (ThaumaturgeTrinketsCompat.isActive()) {
            return ThaumaturgeTrinketsCompat.extractTrinket(entity, item);
        }
        return ItemStack.EMPTY;
    }

    public static List<ItemStack> equippedInEither(LivingEntity entity) {
        if (entity == null) {
            return List.of();
        }
        List<ItemStack> stacks = new ArrayList<>();
        if (ModList.get().isLoaded(TCIds.CURIOS)) {
            stacks.addAll(ThaumaturgeCuriosCompat.equippedCurios(entity));
        }
        if (ThaumaturgeTrinketsCompat.isActive()) {
            stacks.addAll(ThaumaturgeTrinketsCompat.equippedTrinkets(entity));
        }
        return stacks;
    }

    public static boolean rechargeFirstEquipped(Player player) {
        if (player == null) {
            return false;
        }
        if (ModList.get().isLoaded(TCIds.CURIOS) && ThaumaturgeCuriosCompat.rechargeFirstCurio(player)) {
            return true;
        }
        return ThaumaturgeTrinketsCompat.isActive() && ThaumaturgeTrinketsCompat.rechargeFirstTrinket(player);
    }

    public static List<CurioPouchRef> equippedPouchesInEither(LivingEntity entity, Predicate<ItemStack> predicate) {
        List<CurioPouchRef> refs = new ArrayList<>();
        if (entity == null || predicate == null) {
            return refs;
        }
        if (ModList.get().isLoaded(TCIds.CURIOS)) {
            refs.addAll(ThaumaturgeCuriosCompat.equippedPouches(entity, predicate));
        }
        if (ThaumaturgeTrinketsCompat.isActive()) {
            List<TrinketSlotAccess> matched = new ArrayList<>();
            TrinketsApi.getAttachment(entity).forEach((slot, stack) -> {
                if (!stack.isEmpty() && slot.isValid() && predicate.test(stack)) {
                    matched.add(slot);
                }
            });
            if (!matched.isEmpty()) {
                IItemHandlerModifiable adapter = new TrinketSlotAdapter(matched);
                for (int index = 0; index < matched.size(); index++) {
                    refs.add(new CurioPouchRef(adapter, index));
                }
            }
        }
        return refs;
    }
}

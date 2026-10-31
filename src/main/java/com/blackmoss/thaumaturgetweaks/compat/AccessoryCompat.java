package com.blackmoss.thaumaturgetweaks.compat;

import com.blackmoss.thaumaturgetweaks.compat.trinkets.ThaumaturgeTrinketsCompat;
import com.blackmoss.thaumaturgetweaks.compat.trinkets.TrinketState;
import com.leclowndu93150.thaumaturge.TTIds;
import com.leclowndu93150.thaumaturge.compat.curio.ThaumaturgeCuriosCompat;
import com.leclowndu93150.thaumaturge.compat.curio.ThaumaturgeCuriosCompat.CurioPouchRef;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class AccessoryCompat {
    private AccessoryCompat() {
    }

    public static boolean isAnyAccessoryModLoaded() {
        return ModList.get().isLoaded(TTIds.CURIOS) || TrinketState.isLoaded();
    }

    public static boolean isEquipped(LivingEntity entity, Item item) {
        if (entity == null || item == null) {
            return false;
        }
        if (ModList.get().isLoaded(TTIds.CURIOS) && ThaumaturgeCuriosCompat.isCurioEquipped(entity, item)) {
            return true;
        }
        return TrinketState.isLoaded() && ThaumaturgeTrinketsCompat.isEquipped(entity, item);
    }

    public static ItemStack extractEquipped(LivingEntity entity, @Nullable Item item) {
        if (entity == null || item == null) {
            return ItemStack.EMPTY;
        }
        if (ModList.get().isLoaded(TTIds.CURIOS)) {
            ItemStack removed = ThaumaturgeCuriosCompat.extractCurio(entity, item);
            if (!removed.isEmpty()) {
                return removed;
            }
        }
        if (TrinketState.isLoaded()) {
            return ThaumaturgeTrinketsCompat.extractTrinket(entity, item);
        }
        return ItemStack.EMPTY;
    }

    public static List<ItemStack> equippedInEither(LivingEntity entity) {
        if (entity == null) {
            return List.of();
        }
        List<ItemStack> stacks = new ArrayList<>();
        if (ModList.get().isLoaded(TTIds.CURIOS)) {
            stacks.addAll(ThaumaturgeCuriosCompat.equippedCurios(entity));
        }
        if (TrinketState.isLoaded()) {
            stacks.addAll(ThaumaturgeTrinketsCompat.equippedTrinkets(entity));
        }
        return stacks;
    }

    public static boolean rechargeFirstEquipped(Player player) {
        if (player == null) {
            return false;
        }
        if (ModList.get().isLoaded(TTIds.CURIOS) && ThaumaturgeCuriosCompat.rechargeFirstCurio(player)) {
            return true;
        }
        return TrinketState.isLoaded() && ThaumaturgeTrinketsCompat.rechargeFirstTrinket(player);
    }

    public static List<CurioPouchRef> equippedPouchesInEither(LivingEntity entity, Predicate<ItemStack> predicate) {
        List<CurioPouchRef> refs = new ArrayList<>();
        if (entity == null || predicate == null) {
            return refs;
        }
        if (ModList.get().isLoaded(TTIds.CURIOS)) {
            refs.addAll(ThaumaturgeCuriosCompat.equippedPouches(entity, predicate));
        }
        if (TrinketState.isLoaded()) {
            refs.addAll(ThaumaturgeTrinketsCompat.equippedPouches(entity, predicate));
        }
        return refs;
    }
}

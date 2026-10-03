package com.blackmoss.thaumaturgetweaks.compat.trinkets;

import eu.pb4.trinkets.api.TrinketSlotAccess;
import eu.pb4.trinkets.api.TrinketSlotUtils;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jspecify.annotations.NonNull;

import java.util.List;

public final class TrinketSlotAdapter implements IItemHandlerModifiable {
    private final List<TrinketSlotAccess> slots;

    public TrinketSlotAdapter(List<TrinketSlotAccess> slots) {
        this.slots = List.copyOf(slots);
    }

    @Override
    public int getSlots() {
        return slots.size();
    }

    @Override
    public @NonNull ItemStack getStackInSlot(int slot) {
        return isInRange(slot) ? slots.get(slot).get() : ItemStack.EMPTY;
    }

    @Override
    public void setStackInSlot(int slot, @NonNull ItemStack stack) {
        if (isInRange(slot)) {
            slots.get(slot).set(stack);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return isInRange(slot) ? slots.get(slot).maxStackSize(getStackInSlot(slot)) : 0;
    }

    @Override
    public boolean isItemValid(int slot, @NonNull ItemStack stack) {
        return isInRange(slot) && TrinketSlotUtils.mayPlace(slots.get(slot), stack);
    }

    @Override
    public @NonNull ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty() || !isItemValid(slot, stack)) {
            return stack;
        }
        ItemStack current = getStackInSlot(slot);
        if (current.isEmpty()) {
            if (!simulate) {
                setStackInSlot(slot, stack);
            }
            return ItemStack.EMPTY;
        }
        if (!ItemStack.isSameItemSameComponents(current, stack)) {
            return stack;
        }
        int limit = Math.min(getSlotLimit(slot), stack.getMaxStackSize());
        int room = limit - current.getCount();
        if (room <= 0) {
            return stack;
        }
        int moved = Math.min(room, stack.getCount());
        if (!simulate) {
            current.grow(moved);
            setStackInSlot(slot, current);
        }
        return stack.copyWithCount(stack.getCount() - moved);
    }

    @Override
    public @NonNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack current = getStackInSlot(slot);
        if (current.isEmpty() || amount <= 0) {
            return ItemStack.EMPTY;
        }
        int taken = Math.min(amount, current.getCount());
        ItemStack result = current.copyWithCount(taken);
        if (!simulate) {
            ItemStack left = current.copyWithCount(current.getCount() - taken);
            setStackInSlot(slot, left);
        }
        return result;
    }

    private boolean isInRange(int slot) {
        return slot >= 0 && slot < slots.size();
    }
}

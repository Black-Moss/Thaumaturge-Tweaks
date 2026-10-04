package com.blackmoss.thaumaturgetweaks.mixin.trinkets.client;

import com.blackmoss.thaumaturgetweaks.compat.AccessoryCompat;
import com.leclowndu93150.thaumaturge.api.items.IRechargable;
import com.leclowndu93150.thaumaturge.api.items.RechargeAccess;
import com.leclowndu93150.thaumaturge.client.hud.RechargeHudOverlay;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.HashMap;
import java.util.Map;

@Mixin(RechargeHudOverlay.class)
public class RechargeHudOverlayMixin {
    @Unique
    private static final int PERIODIC_SHOW_TICKS = 60;
    @Unique
    private final Map<Integer, Integer> thaumaturgetweaks$accessoryCharge = new HashMap<>();
    @Unique
    private final Map<Integer, Integer> thaumaturgetweaks$accessoryChangeTick = new HashMap<>();
    @Shadow
    @Final
    private Map<EquipmentSlot, Integer> lastCharge;
    @Shadow
    @Final
    private Map<EquipmentSlot, Integer> changeTick;

    @Invoker("drawMeter")
    private static void thaumaturgetweaks$drawMeter(GuiGraphicsExtractor graphics, Minecraft mc, ItemStack stack,
                                                    int max, int charge, int index, boolean showAmount) {
    }

    @WrapMethod(method = "render")
    private void thaumaturgetweaks$render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, Operation<Void> original) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.options.hideGui) {
            return;
        }
        int shown = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.getItem() instanceof IRechargable rechargable) {
                IRechargable.ChargeDisplay display = rechargable.showInHud(stack, player);
                if (display != IRechargable.ChargeDisplay.NEVER) {
                    int charge = RechargeAccess.getCharge(stack);
                    Integer previous = lastCharge.put(slot, charge);
                    if (previous == null || previous != charge) {
                        changeTick.put(slot, player.tickCount);
                    }
                    boolean held = slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND;
                    if (held || display != IRechargable.ChargeDisplay.PERIODIC
                            || player.tickCount - changeTick.getOrDefault(slot, Integer.MIN_VALUE) <= PERIODIC_SHOW_TICKS) {
                        thaumaturgetweaks$drawMeter(graphics, mc, stack, rechargable.getMaxCharge(stack, player), charge,
                                shown++, player.isShiftKeyDown());
                    }
                }
            } else {
                lastCharge.remove(slot);
                changeTick.remove(slot);
            }
        }
        for (ItemStack stack : AccessoryCompat.equippedInEither(player)) {
            if (!(stack.getItem() instanceof IRechargable rechargable)) {
                continue;
            }
            IRechargable.ChargeDisplay display = rechargable.showInHud(stack, player);
            if (display == IRechargable.ChargeDisplay.NEVER) {
                continue;
            }
            int charge = RechargeAccess.getCharge(stack);
            int key = stack.hashCode();
            Integer previous = thaumaturgetweaks$accessoryCharge.put(key, charge);
            if (previous == null || previous != charge) {
                thaumaturgetweaks$accessoryChangeTick.put(key, player.tickCount);
            }
            if (display == IRechargable.ChargeDisplay.PERIODIC
                    && player.tickCount - thaumaturgetweaks$accessoryChangeTick.getOrDefault(key, Integer.MIN_VALUE) > PERIODIC_SHOW_TICKS) {
                continue;
            }
            thaumaturgetweaks$drawMeter(
                    graphics,
                    mc,
                    stack,
                    rechargable.getMaxCharge(stack, player), charge, shown++,
                    player.isShiftKeyDown());
        }
    }
}

package com.blackmoss.thaumaturgetweaks.mixin.client.screen.research;

import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import com.leclowndu93150.thaumaturge.client.screen.research.ResearchTableScreen;
import com.leclowndu93150.thaumaturge.content.aspect.AspectCombinations;
import com.leclowndu93150.thaumaturge.content.research.note.HexGrid;
import com.leclowndu93150.thaumaturge.content.research.note.ResearchNoteData;
import com.leclowndu93150.thaumaturge.content.research.pool.AspectPools;
import com.leclowndu93150.thaumaturge.content.research.table.BlockEntityResearchTable;
import com.leclowndu93150.thaumaturge.content.research.table.MenuResearchTable;
import com.leclowndu93150.thaumaturge.network.ServerboundTableCombinePayload;
import com.leclowndu93150.thaumaturge.network.ServerboundTablePlaceAspectPayload;
import com.leclowndu93150.thaumaturge.registry.TCSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.Optional;

@Mixin(ResearchTableScreen.class)
public abstract class ResearchTableScreenMixin {
    @Unique
    private static final int SHIFT_COMBINE_BATCH = 10;

    @Unique
    private static int thaumaturgeTweaks$maxCombinations(
            Player player,
            @org.jetbrains.annotations.Nullable BlockEntityResearchTable table,
            Holder<IAspect> first,
            Holder<IAspect> second) {
        return Math.min(thaumaturgeTweaks$available(player, table, first), thaumaturgeTweaks$available(player, table, second));
    }

    @Unique
    private static int thaumaturgeTweaks$available(
            Player player,
            @org.jetbrains.annotations.Nullable BlockEntityResearchTable table,
            Holder<IAspect> aspect) {
        int amount = AspectPools.amount(player, aspect);
        if (table != null) {
            amount += table.bonusAspects().amountOf(aspect);
        }
        return amount;
    }

    @Unique
    private static boolean thaumaturgeTweaks$isBonusSource(
            Player player,
            @org.jetbrains.annotations.Nullable BlockEntityResearchTable table,
            Holder<IAspect> aspect) {
        return table != null && AspectPools.amount(player, aspect) <= 0 && table.bonusAspects().amountOf(aspect) > 0;
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    private void thaumaturgetweaks$combineOnPaletteDrop(
            double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        ResearchTableScreenAccessor self = (ResearchTableScreenAccessor) this;
        Holder<IAspect> dragged = self.thaumaturgetweaks$draggedAspect();
        if (dragged == null || button != 0) {
            return;
        }
        Holder<IAspect> target = self.thaumaturgetweaks$paletteAspectAt(mouseX, mouseY);
        if (target == null || Objects.equals(target.getKey(), dragged.getKey())) {
            return;
        }
        Player player = Minecraft.getInstance().player;
        MenuResearchTable menu = ((ResearchTableScreen) (Object) this).getMenu();
        if (player == null || menu == null) {
            return;
        }

        if (AspectCombinations.result(player.level().registryAccess(), dragged, target) == null) {
            return;
        }
        BlockEntityResearchTable table = self.thaumaturgetweaks$table();
        boolean batch = Minecraft.getInstance().options.keyShift.isDown();
        int count = batch ? Math.min(SHIFT_COMBINE_BATCH, thaumaturgeTweaks$maxCombinations(player, table, dragged, target)) : 1;
        for (int i = 0; i < count; i++) {
            boolean bonus1 = thaumaturgeTweaks$isBonusSource(player, table, dragged);
            boolean bonus2 = thaumaturgeTweaks$isBonusSource(player, table, target);
            PacketDistributor.sendToServer(new ServerboundTableCombinePayload(
                    menu.pos(), AspectPools.idOf(dragged), AspectPools.idOf(target), bonus1, bonus2)
            );
        }
        self.thaumaturgetweaks$setDraggedAspect(null);
        player.playSound(TCSounds.HHON.get(), 0.3F, 1.0F);
        cir.setReturnValue(true);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void thaumaturgetweaks$eraseOnRightClick(
            double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (button != 1) {
            return;
        }
        ResearchTableScreenAccessor self = (ResearchTableScreenAccessor) this;
        HexGrid.Hex hex = self.thaumaturgetweaks$hexAt(mouseX, mouseY);
        if (hex == null) {
            return;
        }
        ResearchNoteData data = self.thaumaturgetweaks$noteData();
        if (data == null || data.complete()) {
            return;
        }
        ResearchNoteData.Cell cell = data.cellAt(hex);
        if (cell == null || cell.type() != ResearchNoteData.TYPE_PLACED) {
            return;
        }
        MenuResearchTable menu = ((ResearchTableScreen) (Object) this).getMenu();
        if (menu == null) {
            return;
        }
        PacketDistributor.sendToServer(
                new ServerboundTablePlaceAspectPayload(menu.pos(), hex.q(), hex.r(), Optional.empty()));
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            player.playSound(TCSounds.ERASE.get(), 0.2F, 1.0F);
        }
        cir.setReturnValue(true);
    }
}

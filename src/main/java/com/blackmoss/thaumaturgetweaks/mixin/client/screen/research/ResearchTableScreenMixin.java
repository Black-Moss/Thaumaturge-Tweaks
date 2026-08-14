// 研究台拖拽合成增强：
// 将调色板中的要素拖到另一个要素上释放即可合成（复用注魔台 combine 逻辑）。
// 按住 Shift 释放时批量合成 10 次（材料不足则按实际可用量尽量合成）。
// 服务端 combineAspects 每轮消耗两个输入各 1、产出 1；组合无效时服务端仍会消耗输入，
// 因此发送前用 AspectCombinations.result 前置校验，避免无效组合白扣材料。
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
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.Optional;

@Mixin(ResearchTableScreen.class)
public abstract class ResearchTableScreenMixin {

    // Shift 批量合成次数。
    @Unique
    private static final int SHIFT_COMBINE_BATCH = 10;

    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    private void thaumaturgetweaks$combineOnPaletteDrop(
            MouseButtonEvent event, CallbackInfoReturnable<Boolean> cir) {
        ResearchTableScreenAccessor self = (ResearchTableScreenAccessor) this;
        Holder<IAspect> dragged = self.thaumaturgetweaks$draggedAspect();
        if (dragged == null || event.button() != 0) {
            return;
        }
        // 释放位置必须落在调色板的另一个要素上。
        Holder<IAspect> target = self.thaumaturgetweaks$paletteAspectAt(event.x(), event.y());
        if (target == null || Objects.equals(target.getKey(), dragged.getKey())) {
            return;
        }
        Player player = Minecraft.getInstance().player;
        // menu 字段定义在父类 AbstractContainerScreen，经其 public getMenu() 获取。
        MenuResearchTable menu = ((ResearchTableScreen) (Object) this).getMenu();
        if (player == null || menu == null) {
            return;
        }

        // 组合无效时服务端也会消耗输入，前置校验避免白扣。
        if (AspectCombinations.result(player.level().registryAccess(), dragged, target) == null) {
            return;
        }
        BlockEntityResearchTable table = self.thaumaturgetweaks$table();
        boolean batch = Minecraft.getInstance().hasShiftDown();
        int count = batch ? Math.min(SHIFT_COMBINE_BATCH, thaumaturgeTweaks$maxCombinations(player, table, dragged, target)) : 1;
        for (int i = 0; i < count; i++) {
            boolean bonus1 = thaumaturgeTweaks$isBonusSource(player, table, dragged);
            boolean bonus2 = thaumaturgeTweaks$isBonusSource(player, table, target);
            ClientPacketDistributor.sendToServer(new ServerboundTableCombinePayload(
                    menu.pos(), AspectPools.idOf(dragged), AspectPools.idOf(target), bonus1, bonus2)
            );
        }
        self.thaumaturgetweaks$setDraggedAspect(null);
        player.playSound(TCSounds.HHON.get(), 0.3F, 1.0F);
        cir.setReturnValue(true);
    }

    // 右键擦除增强：右键点击研究纸张上已放置要素（TYPE_PLACED）的六边形格子，
    // 发送空要素放置请求（Optional.empty()）将其擦除，行为与左键擦除一致。
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void thaumaturgetweaks$eraseOnRightClick(
            MouseButtonEvent event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
        if (event.button() != 1) {
            return;
        }
        ResearchTableScreenAccessor self = (ResearchTableScreenAccessor) this;
        HexGrid.Hex hex = self.thaumaturgetweaks$hexAt(event.x(), event.y());
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
        ClientPacketDistributor.sendToServer(
                new ServerboundTablePlaceAspectPayload(menu.pos(), hex.q(), hex.r(), Optional.empty()));
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            player.playSound(TCSounds.ERASE.get(), 0.2F, 1.0F);
        }
        cir.setReturnValue(true);
    }

    // 两个输入各自可提供的最大合成次数（玩家池 + 研究台 bonus），取较小者。
    @Unique
    private static int thaumaturgeTweaks$maxCombinations(
            Player player, @org.jetbrains.annotations.Nullable BlockEntityResearchTable table,
            Holder<IAspect> first, Holder<IAspect> second) {
        return Math.min(thaumaturgeTweaks$available(player, table, first), thaumaturgeTweaks$available(player, table, second));
    }

    @Unique
    private static int thaumaturgeTweaks$available(
            Player player, @org.jetbrains.annotations.Nullable BlockEntityResearchTable table,
            Holder<IAspect> aspect) {
        int amount = AspectPools.amount(player, aspect);
        if (table != null) {
            amount += table.bonusAspects().amountOf(aspect);
        }
        return amount;
    }

    // 与 ResearchTableScreen.handleCombineButton 相同的 bonus 判定：
    // 玩家池为 0 时改用研究台 bonus 要素源。
    @Unique
    private static boolean thaumaturgeTweaks$isBonusSource(
            Player player, @org.jetbrains.annotations.Nullable BlockEntityResearchTable table,
            Holder<IAspect> aspect) {
        return table != null && AspectPools.amount(player, aspect) <= 0 && table.bonusAspects().amountOf(aspect) > 0;
    }
}

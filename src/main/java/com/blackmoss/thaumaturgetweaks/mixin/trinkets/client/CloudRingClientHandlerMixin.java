package com.blackmoss.thaumaturgetweaks.mixin.trinkets.client;

import com.leclowndu93150.thaumaturge.client.equipment.CloudRingClientHandler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CloudRingClientHandler.class)
public class CloudRingClientHandlerMixin {
//    @ModifyExpressionValue(
//            method = "onClientTick",
//            at = @At("HEAD")
//    )
//    private boolean onClientTick(boolean original) {
//        return original;
//    }
//}
//
////    @SubscribeEvent
////    public static void onClientTick(ClientTickEvent.Post event) {
////        LocalPlayer player = Minecraft.getInstance().player;
////        if (player != null && ModList.get().isLoaded("curios") && ThaumaturgeCuriosCompat.isCurioEquipped(player, TCItems.CLOUD_RING.get())) {
////          ...
////        }
//// }
//
////    @SubscribeEvent
////    public static void onClientTick(ClientTickEvent.Post event) {
////        LocalPlayer player = Minecraft.getInstance().player;
////        if (
////          player != null
////          && (ModList.get().isLoaded("curios") || (ModList.get().isLoaded("trinkets"))
////          && (ThaumaturgeCuriosCompat.isCurioEquipped(player, TCItems.CLOUD_RING.get()) || ThaumaturgeTrinkentsCompat.isEquipped(player, TCItems.CLOUD_RING.get()))) {
////          ...
////        }
}
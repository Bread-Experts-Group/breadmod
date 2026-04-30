package org.bread_experts_group.breadmod.mixin.common;

import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(InventoryChangeTrigger.class)
abstract class MixinInventoryChangeTrigger {
	// TODO: FIXUP
//	@Inject(method = "trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/item/ItemStack;)V", at = @At("TAIL"))
//	private void onTrigger(ServerPlayer player, Inventory inventory, ItemStack stack, CallbackInfo ci) {
//		InventoryChangeEvent.onInventoryChange(player, inventory, stack);
//	}
}

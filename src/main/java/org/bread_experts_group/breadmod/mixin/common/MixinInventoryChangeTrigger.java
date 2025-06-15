package org.bread_experts_group.breadmod.mixin.common;

import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.bread_experts_group.breadmod.event.InventoryChangeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryChangeTrigger.class)
abstract class MixinInventoryChangeTrigger {
	@Inject(method = "trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/item/ItemStack;)V", at = @At("TAIL"))
	private void onTrigger(ServerPlayer player, Inventory inventory, ItemStack stack, CallbackInfo ci) {
		InventoryChangeEvent.onInventoryChange(player, inventory, stack);
	}
}

package org.bread_experts_group.breadmod.mixin.common;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.CommonHooks;
import org.bread_experts_group.breadmod.registry.attachment.ModAttachments;
import org.bread_experts_group.breadmod.registry.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
abstract class MixinLivingEntity {
	@Unique
	private final AttachmentType<Boolean> breadmod$kiAttachment = ModAttachments.INSTANCE.getKEEP_INVENTORY_NEXT_DEATH();

	@Unique
	private LivingEntity breadmod$getThis() {
		return (LivingEntity) (Object) this;
	}

	@Inject(method = "checkTotemDeathProtection", at = @At("RETURN"), cancellable = true)
	private void checkTotemDeathProtection(DamageSource damageSource, CallbackInfoReturnable<? super Boolean> cir) {
		if (!cir.getReturnValueZ() && !damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			Item amuletItem = ModItems.INSTANCE.getAMULET_OF_KEEPING().asItem();
			for (InteractionHand interactionhand : InteractionHand.values()) {
				LivingEntity me = breadmod$getThis();
				ItemStack handStack = me.getItemInHand(interactionhand);
				if (
						handStack.is(amuletItem) &&
								CommonHooks.onLivingUseTotem(me, damageSource, handStack, interactionhand)
				) {
					ItemStack amuletStack = handStack.copy();
					handStack.shrink(1);

					if (me instanceof ServerPlayer serverplayer) {
						serverplayer.awardStat(Stats.ITEM_USED.get(amuletItem), 1);
						CriteriaTriggers.USED_TOTEM.trigger(serverplayer, amuletStack);
						me.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
					}

					me.setData(this.breadmod$kiAttachment, true);
					cir.setReturnValue(true);
				}
			}
		}
	}

	@Inject(method = "dropAllDeathLoot", at = @At("HEAD"), cancellable = true)
	private void dropAllDeathLoot(ServerLevel level, DamageSource damageSource, CallbackInfo ci) {
		LivingEntity me = breadmod$getThis();
		if (me.getData(this.breadmod$kiAttachment)) ci.cancel();
	}
}

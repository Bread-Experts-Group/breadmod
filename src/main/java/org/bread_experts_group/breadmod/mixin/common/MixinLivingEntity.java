package org.bread_experts_group.breadmod.mixin.common;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntity.class)
abstract class MixinLivingEntity {
//	@Unique
//	private final AttachmentType<Boolean> breadmod$kiAttachment = ModAttachments.getKEEP_INVENTORY_NEXT_DEATH().get();
//
//	@Unique
//	private LivingEntity breadmod$getThis() {
//		return (LivingEntity) (Object) this;
//	}
//
//	@Inject(method = "checkTotemDeathProtection", at = @At("RETURN"), cancellable = true)
//	private void checkTotemDeathProtection(DamageSource damageSource, CallbackInfoReturnable<? super Boolean> cir) {
//		if (!cir.getReturnValueZ() && !damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
//			Item amuletItem = ModItems.INSTANCE.getAMULET_OF_KEEPING().asItem();
//			for (InteractionHand interactionhand : InteractionHand.values()) {
//				LivingEntity me = breadmod$getThis();
//				ItemStack handStack = me.getItemInHand(interactionhand);
//				if (
//						handStack.is(amuletItem) &&
//								CommonHooks.onLivingUseTotem(me, damageSource, handStack, interactionhand)
//				) {
//					ItemStack amuletStack = handStack.copy();
//					handStack.shrink(1);
//
//					if (me instanceof ServerPlayer serverplayer) {
//						serverplayer.awardStat(Stats.ITEM_USED.get(amuletItem), 1);
//						CriteriaTriggers.USED_TOTEM.trigger(serverplayer, amuletStack);
//						me.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
//					}
//
//					me.setData(this.breadmod$kiAttachment, true);
//					cir.setReturnValue(true);
//				}
//			}
//		}
//	}
//
//	@Inject(method = "dropAllDeathLoot", at = @At("HEAD"), cancellable = true)
//	private void dropAllDeathLoot(ServerLevel level, DamageSource damageSource, CallbackInfo ci) {
//		LivingEntity me = breadmod$getThis();
//		if (me.getData(this.breadmod$kiAttachment)) ci.cancel();
//	}

	// TODO: ABOVE FIXUP

//	@ModifyVariable(
//			method = "checkFallDamage",
//			at = @At(
//					value = "INVOKE",
//					target = "Lnet/minecraft/world/level/block/state/BlockState;isAir()Z",
//					shift = At.Shift.BEFORE
//			),
//			argsOnly = true
//	)
//	private BlockState checkFallDamage(BlockState state) {
//		LivingEntity me = breadmod$getThis();
//		GridHitResult selected = GeneralKt.blockPhysicsGrid(
//				(grid) -> grid instanceof ClientPhysicsGrid,
//				me.position(),
//				me.position().subtract(0.0, -0.1, 0.0),
//				false,
//				CollisionContext.of(me)
//		);
//		if (selected != null) return selected.getState();
//		return state;
//	}
}

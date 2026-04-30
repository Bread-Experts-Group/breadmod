package org.bread_experts_group.breadmod.mixin.common;

import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayer.class)
abstract class MixinServerPlayer {
//	@Unique
//	private final AttachmentType<Boolean> breadmod$kiAttachment = ModAttachments.getKEEP_INVENTORY_NEXT_DEATH().get();
//
//	@Unique
//	private ServerPlayer breadmod$getThis() {
//		return (ServerPlayer) (Object) this;
//	}
//
//	@Inject(method = "restoreFrom", at = @At("HEAD"))
//	private void restoreFrom(ServerPlayer fromServerPlayer, boolean keepEverything, CallbackInfo ci) {
//		if (fromServerPlayer.hasData(this.breadmod$kiAttachment)) {
//			ServerPlayer me = breadmod$getThis();
//			me.getInventory().replaceWith(fromServerPlayer.getInventory());
//			me.experienceLevel = fromServerPlayer.experienceLevel;
//			me.totalExperience = fromServerPlayer.totalExperience;
//			me.experienceProgress = fromServerPlayer.experienceProgress;
//			me.setScore(fromServerPlayer.getScore());
//
//			fromServerPlayer.removeData(this.breadmod$kiAttachment);
//			me.removeData(this.breadmod$kiAttachment);
//		}
//	}
	// TODO: FIXUP
}

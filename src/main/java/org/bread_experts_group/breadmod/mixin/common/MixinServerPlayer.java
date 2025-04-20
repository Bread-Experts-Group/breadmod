package org.bread_experts_group.breadmod.mixin.common;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.bread_experts_group.breadmod.registry.attachment.ModAttachments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
abstract class MixinServerPlayer {
	@Unique
	private final AttachmentType<Boolean> breadmod$kiAttachment = ModAttachments.INSTANCE.getKEEP_INVENTORY_NEXT_DEATH().get();

	@Unique
	private ServerPlayer breadmod$getThis() {
		return (ServerPlayer) (Object) this;
	}

	@Inject(method = "restoreFrom", at = @At("HEAD"))
	private void restoreFrom(ServerPlayer fromServerPlayer, boolean keepEverything, CallbackInfo ci) {
		if (fromServerPlayer.hasData(this.breadmod$kiAttachment)) {
			ServerPlayer me = breadmod$getThis();
			me.getInventory().replaceWith(fromServerPlayer.getInventory());
			me.experienceLevel = fromServerPlayer.experienceLevel;
			me.totalExperience = fromServerPlayer.totalExperience;
			me.experienceProgress = fromServerPlayer.experienceProgress;
			me.setScore(fromServerPlayer.getScore());

			fromServerPlayer.removeData(this.breadmod$kiAttachment);
			me.removeData(this.breadmod$kiAttachment);
		}
	}
}

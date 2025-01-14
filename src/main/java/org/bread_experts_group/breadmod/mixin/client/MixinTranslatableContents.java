package org.bread_experts_group.breadmod.mixin.client;

import net.minecraft.locale.Language;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TranslatableContents.class)
public abstract class MixinTranslatableContents {
	@Inject(method = "<init>", at = @At("TAIL"))
	private void initLangCheck(
			String key, String fallback, Object[] args, CallbackInfo ci
	) {
		if (key.contains("breadmod")) Language.getInstance().has(key);
	}
}

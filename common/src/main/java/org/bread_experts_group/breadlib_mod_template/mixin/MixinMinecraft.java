package org.bread_experts_group.breadlib_mod_template.mixin;

import net.minecraft.client.Minecraft;
import org.bread_experts_group.breadlib_mod_template.BreadLibModTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {

	@Inject(at = @At("TAIL"), method = "<init>")
	private void init(CallbackInfo info) {
		BreadLibModTemplate.LOGGER.info("This line is printed by the Breadlib Mod Template common mixin!");
		BreadLibModTemplate.LOGGER.info("MC Version: {}", Minecraft.getInstance().getVersionType());
	}
}

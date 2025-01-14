package org.bread_experts_group.breadmod.mixin.client;

import net.minecraft.SharedConstants;
import net.minecraft.client.resources.language.ClientLanguage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bread_experts_group.breadmod.BreadMod;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Map;

@Mixin(ClientLanguage.class)
public abstract class MixinClientLanguage {
	@Shadow
	@Final
	private Map<String, String> storage;

	@Unique
	Logger breadmod$logger = LogManager.getLogger("BreadMod ClientLanguage Debug");

	@Unique
	HashSet<String> breadmod$warned = new HashSet<>();

	@Inject(method = "has", at = @At("HEAD"), cancellable = true)
	private void hasForDebug(
			String id,
			CallbackInfoReturnable<Boolean> cir
	) {
		final boolean has = this.storage.containsKey(id);
		if (SharedConstants.IS_RUNNING_IN_IDE && !has && id.contains(BreadMod.ID) && !breadmod$warned.contains(id)) {
			breadmod$logger.warn("No language entry defined for ID: {}", id);
			breadmod$warned.add(id);
		}
		cir.setReturnValue(has);
	}
}

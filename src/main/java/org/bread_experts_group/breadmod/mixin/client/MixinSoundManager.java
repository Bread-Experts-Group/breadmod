package org.bread_experts_group.breadmod.mixin.client;

import net.minecraft.SharedConstants;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(SoundManager.class)
public abstract class MixinSoundManager {
	@Shadow
	@Final
	private SoundEngine soundEngine;

	@Shadow
	@Final
	private Map<ResourceLocation, WeighedSoundEvents> registry;

	@SuppressWarnings("LongLine")
	@Inject(method = "apply(Lnet/minecraft/client/sounds/SoundManager$Preparations;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"), cancellable = true)
	private void pleaseShutUpApply(
			SoundManager.Preparations object, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci
	) {
		if (SharedConstants.IS_RUNNING_IN_IDE) {
			ci.cancel();
			this.soundEngine.reload();
			for (ResourceLocation resourcelocation : this.registry.keySet()) {
				WeighedSoundEvents weighedsoundevents = this.registry.get(resourcelocation);
				Component subtitle = weighedsoundevents.getSubtitle();
				if (subtitle == null) continue;
				ComponentContents contents = subtitle.getContents();
				if (contents instanceof TranslatableContents)
					Language.getInstance().has(((TranslatableContents) contents).getKey());
			}
		}
	}
}

package org.bread_experts_group.breadmod.mixin.client;

import com.mojang.blaze3d.audio.Channel;
import net.minecraft.client.sounds.AudioStream;
import org.bread_experts_group.breadmod.client.sound.RIFFAudioStream;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Channel.class)
abstract class MixinChannel {
	@Shadow @Nullable public AudioStream stream;

	@Inject(method = "unpause", at = @At("HEAD"), cancellable = true)
	private void shortCircuitUnpause(CallbackInfo ci) {
		if (this.stream instanceof RIFFAudioStream riffStream) {
			if (riffStream.isPaused()) ci.cancel();
		}
	}
}
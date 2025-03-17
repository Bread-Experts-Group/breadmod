package org.bread_experts_group.breadmod.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.bread_experts_group.breadmod.client.gui.overlays.ScreenBleedOverlay;
import org.bread_experts_group.breadmod.client.gui.screens.BlueScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPacketListener.class)
abstract class ClientPacketListenerMixin {
	@Redirect(
			method = "handlePlayerCombatKill",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V"
			)
	)
	public void handlePlayerCombatKill(final Minecraft instance, final Screen old) {
		if (ScreenBleedOverlay.Companion.getOverrideDeathScreen()) {
			instance.setScreen(new BlueScreen());
		} else instance.setScreen(old);
	}
}

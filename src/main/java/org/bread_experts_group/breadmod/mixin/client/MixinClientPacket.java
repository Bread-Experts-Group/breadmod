package org.bread_experts_group.breadmod.mixin.client;

import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientPacketListener.class)
abstract class MixinClientPacket {
//	@Redirect(
//			method = "handlePlayerCombatKill",
//			at = @At(
//					value = "INVOKE",
//					target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V"
//			)
//	)
//	private void handlePlayerCombatKill(Minecraft instance, Screen old) {
//		if (ScreenBleedOverlay.Companion.getOverrideDeathScreen()) {
//			instance.setScreen(new BlueScreen());
//			var minecraft = Minecraft.getInstance();
//			LocalPlayer player = minecraft.player;
//			if (player != null) player.setShowDeathScreen(false);
//		}
//	}
	// TODO: FIXUP
}
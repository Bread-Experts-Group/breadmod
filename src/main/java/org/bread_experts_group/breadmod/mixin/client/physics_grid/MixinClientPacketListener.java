package org.bread_experts_group.breadmod.mixin.client.physics_grid;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid;
import org.bread_experts_group.breadmod.util.GeneralKt;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPacketListener.class)
abstract class MixinClientPacketListener {
	@ModifyExpressionValue(
			method = "handleBlockEntityData",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/Minecraft;level:Lnet/minecraft/client/multiplayer/ClientLevel;",
					opcode = Opcodes.GETFIELD
			)
	)
	private ClientLevel redirectLevel(ClientLevel original, @Local(argsOnly = true) ClientboundBlockEntityDataPacket packet) {
		GeneralKt.logDebugInfo("Receiving client packet to level: " + packet.getType() + ", " + packet.getTag());
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null) {
			PhysicsGrid grid = PhysicsGrid.getClosestGrid(player);
			if (grid != null) {
				GeneralKt.logDebugInfo("Receiving client packet to grid: " + packet.getType() + ", " + packet.getTag());
				return (ClientLevel) grid.microLevel;
			}
		}
		return original;
	}
}

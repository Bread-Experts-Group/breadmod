package org.bread_experts_group.breadmod.mixin.client.compat.jade;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import snownee.jade.impl.WailaClientRegistration;

@Mixin(WailaClientRegistration.class)
abstract class MixinWailaClientRegistration {
	@ModifyExpressionValue(
			method = "blockAccessor",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/Minecraft;level:Lnet/minecraft/client/multiplayer/ClientLevel;",
					opcode = Opcodes.GETFIELD
			)
	)
	private ClientLevel redirectLevel(ClientLevel original) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) return original;
		PhysicsGrid grid = PhysicsGrid.getClosestGrid(player);
		return (grid != null) ? (ClientLevel) grid.microLevel : original;
	}
}

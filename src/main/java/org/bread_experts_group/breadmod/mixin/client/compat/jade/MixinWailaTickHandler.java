package org.bread_experts_group.breadmod.mixin.client.compat.jade;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import snownee.jade.overlay.WailaTickHandler;

@Mixin(WailaTickHandler.class)
abstract class MixinWailaTickHandler {
	@ModifyExpressionValue(
			method = "tickClient",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/Minecraft;level:Lnet/minecraft/client/multiplayer/ClientLevel;",
					opcode = Opcodes.GETFIELD
			)
	)
	private ClientLevel redirectLevel(ClientLevel original) {
		Level gridLevel = PhysicsGrid.redirectLevelToGrid(original);
		return (gridLevel != null) ? (ClientLevel) gridLevel : original;
	}
}

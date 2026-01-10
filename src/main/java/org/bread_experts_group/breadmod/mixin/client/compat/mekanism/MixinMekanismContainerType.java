package org.bread_experts_group.breadmod.mixin.client.compat.mekanism;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mekanism.common.inventory.container.type.MekanismContainerType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MekanismContainerType.class)
public class MixinMekanismContainerType {
	@ModifyExpressionValue(
			method = "getTileFromBuf",
			at = @At(value = "FIELD",
					target = "Lnet/minecraft/client/Minecraft;level:Lnet/minecraft/client/multiplayer/ClientLevel;",
					opcode = Opcodes.GETFIELD
			)
	)
	private static ClientLevel redirectLevelForGrid(ClientLevel original) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) return original;
		PhysicsGrid grid = PhysicsGrid.getClosestGrid(player);
		return (grid != null) ? (ClientLevel) grid.microLevel : original;
	}
}

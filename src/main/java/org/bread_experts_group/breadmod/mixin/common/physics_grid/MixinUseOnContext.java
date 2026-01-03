package org.bread_experts_group.breadmod.mixin.common.physics_grid;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(UseOnContext.class)
abstract class MixinUseOnContext {
	@ModifyVariable(
			method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/phys/BlockHitResult;)V",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/world/item/context/UseOnContext;level:Lnet/minecraft/world/level/Level;",
					opcode = Opcodes.PUTFIELD
			),
			argsOnly = true
	)
	private Level redirectLevelForGrid(Level value, @Local(argsOnly = true) Player player) {
		PhysicsGrid grid = PhysicsGrid.getClosestGrid(player);
		return (grid != null) ? grid.microLevel : value;
	}
}
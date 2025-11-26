package org.bread_experts_group.breadmod.experimental.camera_viewer.item

import net.minecraft.core.component.DataComponents
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import org.bread_experts_group.breadmod.ModDataComponents
import org.bread_experts_group.breadmod.experimental.camera_viewer.CameraViewerBlock
import org.bread_experts_group.breadmod.experimental.camera_viewer.CameraViewerHandler
import org.bread_experts_group.breadmod.experimental.camera_viewer.camera.CameraBlock
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity

class CameraBinderItem : Item(Properties()) {
	override fun useOn(context: UseOnContext): InteractionResult {
		val stack = context.itemInHand
		val level = context.level
		val pos = context.clickedPos
		val state = level.getBlockState(pos)

		if (state.block is CameraBlock) {
			stack.set(ModDataComponents.BLOCK_POS, pos)
			stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
			return InteractionResult.sidedSuccess(level.isClientSide)
		}
		if (state.block is CameraViewerBlock) {
			val boundPos = stack.get(ModDataComponents.BLOCK_POS) ?: return InteractionResult.CONSUME
			val entity = level.getBlockEntity(pos) as BreadModBlockEntity
			val handler = entity.getCapability(CameraViewerHandler.BLOCK_VOID)
			stack.remove(ModDataComponents.BLOCK_POS)
			stack.remove(DataComponents.ENCHANTMENT_GLINT_OVERRIDE)
			handler.boundPos = boundPos
			entity.setChanged()
			return InteractionResult.sidedSuccess(level.isClientSide)
		}
		return InteractionResult.CONSUME
	}
}
package org.bread_experts_group.breadmod.registry.block.actual

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult

class ItemPedestalBlock : BaseEntityBlock(Properties.of()) {
	override fun codec(): MapCodec<out BaseEntityBlock> = BlockBehaviour.simpleCodec { this }

	override fun useItemOn(
		stack: ItemStack,
		state: BlockState,
		level: Level,
		pos: BlockPos,
		player: Player,
		hand: InteractionHand,
		hitResult: BlockHitResult
	): ItemInteractionResult {
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult)
	}

	override fun entityInside(state: BlockState, level: Level, pos: BlockPos, entity: Entity) {
		val itemEntity = entity as? ItemEntity ?: return
	}

	override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL

	override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? = null
}
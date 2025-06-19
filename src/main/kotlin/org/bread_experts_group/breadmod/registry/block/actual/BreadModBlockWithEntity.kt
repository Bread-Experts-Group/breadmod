package org.bread_experts_group.breadmod.registry.block.actual

import com.mojang.serialization.MapCodec
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.ItemInteractionResult.SUCCESS
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.RenderShape.MODEL
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.neoforged.neoforge.fluids.FluidUtil
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity

abstract class BreadModBlockWithEntity(
	properties: Properties
) : BaseEntityBlock(properties) {
	override fun codec(): MapCodec<out BaseEntityBlock> = BaseEntityBlock.simpleCodec { this }
	override fun getRenderShape(state: BlockState): RenderShape = MODEL

	open fun useItemOnBM(
		stack: ItemStack,
		state: BlockState,
		level: Level,
		pos: BlockPos,
		player: Player,
		hand: InteractionHand,
		hitResult: BlockHitResult
	): ItemInteractionResult = ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION

	final override fun useItemOn(
		stack: ItemStack,
		state: BlockState,
		level: Level,
		pos: BlockPos,
		player: Player,
		hand: InteractionHand,
		hitResult: BlockHitResult
	): ItemInteractionResult {
		if (FluidUtil.interactWithFluidHandler(player, hand, level, pos, hitResult.direction)) {
			return SUCCESS
		}
		return this.useItemOnBM(stack, state, level, pos, player, hand, hitResult)
	}

	/**
	 * Override this to enable [BlockEntity] ticking for this block.
	 */
	open fun getBlockEntityType(level: Level, state: BlockState): BlockEntityType<*>? = null

	final override fun <T : BlockEntity> getTicker(
		level: Level,
		state: BlockState,
		blockEntityType: BlockEntityType<T>
	): BlockEntityTicker<T>? = this.tickBlockEntity(blockEntityType, this.getBlockEntityType(level, state))

	@Suppress("UNCHECKED_CAST")
	private fun <E : BlockEntity, A : BlockEntity> tickBlockEntity(
		serverType: BlockEntityType<A>?, clientType: BlockEntityType<E>?
	): BlockEntityTicker<A>? {
		return if (clientType === serverType) {
			BlockEntityTicker<A> { level, pos, state, blockEntity ->
				blockEntity as BreadModBlockEntity<A>
				if (level.isClientSide) blockEntity.clientTick(level as ClientLevel, pos, state)
				else blockEntity.serverTick(level as ServerLevel, pos, state)
				blockEntity.commonTick(level, pos, state)
			}
		} else null
	}
}
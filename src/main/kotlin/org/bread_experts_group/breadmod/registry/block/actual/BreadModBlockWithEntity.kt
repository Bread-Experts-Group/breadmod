package org.bread_experts_group.breadmod.registry.block.actual

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.RenderShape.MODEL
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.common.SoundActions
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.FluidType
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.FluidBearingBlockEntity

abstract class BreadModBlockWithEntity(
	properties: Properties
) : BaseEntityBlock(properties) {
	override fun codec(): MapCodec<out BaseEntityBlock> = simpleCodec { this }
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
		if (level.isClientSide || hand == InteractionHand.OFF_HAND)
			return this.useItemOnBM(stack, state, level, pos, player, hand, hitResult)
		(level.getBlockEntity(pos) as? BreadModBlockEntity<*>)?.let { entity ->
			val handStack = player.getItemInHand(hand)
			val item = handStack.item
			if (entity is FluidBearingBlockEntity) {
				var fluidStack: FluidStack = FluidStack.EMPTY // todo test this to make sure it isn't broken
				val cap = handStack.getCapability(Capabilities.FluidHandler.ITEM)
				if (item is BucketItem) {
					fluidStack = FluidStack(item.content, FluidType.BUCKET_VOLUME)
				} else cap?.let { fluidStack = it.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.SIMULATE) }
					?: return this.useItemOnBM(handStack, state, level, pos, player, hand, hitResult)
				val sim = entity.fluidHandler.fill(fluidStack, IFluidHandler.FluidAction.SIMULATE)
				if (sim >= FluidType.BUCKET_VOLUME) {
					entity.fluidHandler.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE)
					level.playSound(
						null,
						pos,
						fluidStack.fluidType.getSound(SoundActions.BUCKET_EMPTY) ?: SoundEvents.BUCKET_EMPTY,
						SoundSource.BLOCKS,
						1.0f,
						1.0f
					)
					if (item is BucketItem) {
						if (handStack.count == 1) {
							player.setItemInHand(hand, ItemStack(Items.BUCKET))
						} else {
							player.addItem(ItemStack(Items.BUCKET))
							handStack.shrink(1)
						}
					} else cap?.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE)
					return ItemInteractionResult.SUCCESS
				}
				val drainSim = entity.fluidHandler.drain(
					FluidType.BUCKET_VOLUME,
					IFluidHandler.FluidAction.SIMULATE
				)
				if (drainSim.amount >= 1000) {
					val capFilled = cap?.fill(drainSim, IFluidHandler.FluidAction.SIMULATE) ?: 0
					if (fluidStack.fluidType.isAir || capFilled >= FluidType.BUCKET_VOLUME) {
						entity.fluidHandler.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE)
						if (item is BucketItem) {
							val newStack = drainSim.fluidType.getBucket(drainSim)
							if (handStack.count == 1) {
								player.setItemInHand(hand, newStack)
							} else {
								player.addItem(newStack)
								handStack.shrink(1)
							}
						} else cap?.fill(drainSim, IFluidHandler.FluidAction.EXECUTE)
						level.playSound(
							null,
							pos,
							fluidStack.fluidType.getSound(SoundActions.BUCKET_FILL) ?: SoundEvents.BUCKET_FILL,
							SoundSource.BLOCKS,
							1.0f,
							1.0f
						)
						return ItemInteractionResult.SUCCESS
					}
				}
			}
		}
		return this.useItemOnBM(stack, state, level, pos, player, hand, hitResult)
	}

	protected fun <T : BreadModBlockEntity<T>> tickBreadModBlockEntity(
		level: Level,
		pos: BlockPos,
		state: BlockState,
		blockEntity: T
	) {
		if (level.isClientSide) blockEntity.clientTick(level, pos, state, blockEntity)
		else blockEntity.serverTick(level, pos, state, blockEntity)
		blockEntity.commonTick(level, pos, state, blockEntity)
	}

	override fun <T : BlockEntity> getTicker(
		level: Level,
		state: BlockState,
		blockEntityType: BlockEntityType<T>
	): BlockEntityTicker<T>? = null
}
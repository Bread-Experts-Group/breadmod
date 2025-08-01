package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.DyeItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.BlockHitResult
import net.neoforged.neoforge.capabilities.Capabilities
import org.bread_experts_group.breadmod.client.render.entity.block.EnergyStorageRenderer
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.CapabilityMap
import org.bread_experts_group.breadmod.registry.block.actual.entity.handler.ExtendedEnergyHandler
import org.bread_experts_group.breadmod.registry.block.actual.entity.handler.state.EnergyStorageStateHandler
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockStateProperties
import org.bread_experts_group.breadmod.util.floatRoundEven
import java.math.BigDecimal
import java.util.Optional
import kotlin.math.roundToInt

class EnergyStorageBlock : BreadModBlock(Properties.of()) {
	override fun shouldCreateEntity(with: Pair<BlockPos, BlockState>?): Boolean = true
	override fun ofCapabilities(): CapabilityMap {
		val container = ExtendedEnergyHandler(BigDecimal(10000000))
		val storage = { _: BreadModBlockEntity, _: Any? -> container }
		return mapOf(
			Capabilities.EnergyStorage.BLOCK to mapOf(
				Optional.empty<Direction>() to storage,
				Optional.of(Direction.UP) to storage,
				Optional.of(Direction.DOWN) to storage,
				Optional.of(Direction.NORTH) to storage,
				Optional.of(Direction.SOUTH) to storage,
				Optional.of(Direction.EAST) to storage,
				Optional.of(Direction.WEST) to storage,
			),
			EnergyStorageStateHandler.BLOCK_VOID to mapOf(Optional.empty<Any>() to { _, _ -> EnergyStorageStateHandler() })
		)
	}

	override fun ofRenderer(): ((BlockEntityRendererProvider.Context) -> BlockEntityRenderer<out BreadModBlockEntity>)? =
		::EnergyStorageRenderer

	override val serverTickBM: BreadModTicker<ServerLevel> = { entity, level, state, pos ->
		val energy = entity.getCapability(Capabilities.EnergyStorage.BLOCK) as ExtendedEnergyHandler
		level.setBlockAndUpdate(
			pos,
			state.setValue(
				ModBlockStateProperties.STORAGE_LEVEL,
				((energy.bigAmount.divide(energy.bigCapacity, floatRoundEven)).toFloat() / (1 / 13f))
					.roundToInt()
			)
		)
	}

	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
		builder.add(BlockStateProperties.HORIZONTAL_FACING, ModBlockStateProperties.STORAGE_LEVEL)
	}

	override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
		return this.defaultBlockState()
			.setValue(BlockStateProperties.HORIZONTAL_FACING, context.horizontalDirection.opposite)
	}

	override fun useItemOnBM(
		stack: ItemStack,
		state: BlockState,
		level: Level,
		pos: BlockPos,
		player: Player,
		hand: InteractionHand,
		hitResult: BlockHitResult
	): ItemInteractionResult {
		val dye = stack.item as? DyeItem ?: return super.useItemOnBM(stack, state, level, pos, player, hand, hitResult)
		val entity = level.getBlockEntity(pos) as BreadModBlockEntity
		val eState = entity.getCapability(EnergyStorageStateHandler.BLOCK_VOID)
		eState.set(EnergyStorageStateHandler.COLOR, dye.dyeColor.textColor)
		level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1f, 1f)
		level.sendBlockUpdated(pos, state, state, 3)
		return ItemInteractionResult.sidedSuccess(level.isClientSide)
	}
}
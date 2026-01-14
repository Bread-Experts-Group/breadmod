package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
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
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockStateProperties
import org.bread_experts_group.breadmod.registry.block.handler.ExtendedEnergyHandler
import org.bread_experts_group.breadmod.registry.block.handler.state.EnergyStorageStateHandler
import org.bread_experts_group.breadmod.util.floatRoundEven
import java.math.BigDecimal
import kotlin.math.roundToInt

class EnergyStorageBlock : BreadModBlock(Properties.of()) {
	override fun shouldCreateEntity(with: Pair<BlockPos, BlockState>?): Boolean = true
	override fun ofCapabilities(): CapabilityMap<(BreadModBlockEntity) -> Any> = mapOf(
		this.setupHandlerPair(
			Capabilities.EnergyStorage.BLOCK,
			ExtendedEnergyHandler(BigDecimal(10000000)),
			*BreadModBlock.ALL_DIRECTIONS
		),
		this.setupHandlerPair(EnergyStorageStateHandler.BLOCK_VOID, EnergyStorageStateHandler())
	)

	override fun ofRenderer(): ((BlockEntityRendererProvider.Context) -> BlockEntityRenderer<out BreadModBlockEntity>) =
		::EnergyStorageRenderer

	override val serverTickBM: BreadModTicker<ServerLevel> = { entity, level, state, pos ->
		val energy = entity.getCapability(Capabilities.EnergyStorage.BLOCK) as ExtendedEnergyHandler
		val calculated = ((energy.bigAmount.divide(energy.bigCapacity, floatRoundEven)).toFloat() / (1 / 13f))
			.roundToInt()
		energy.previousLevel = energy.currentLevel
		energy.currentLevel = calculated
		if (energy.previousLevel != energy.currentLevel) level.setBlockAndUpdate(
			pos,
			state.setValue(ModBlockStateProperties.STORAGE_LEVEL, calculated)
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
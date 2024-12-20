package org.bread_experts_group.breadmod.registry.block.actual.machine

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.world.Containers
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.BlockHitResult
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.block.actual.entity.machine.WheatCrusherBlockEntity

class WheatCrusherBlock : BaseEntityBlock(Properties.of()) {
	companion object {
		val CODEC : MapCodec<out BaseEntityBlock> = simpleCodec { WheatCrusherBlock() }
	}

	override fun codec() : MapCodec<out BaseEntityBlock> = Companion.CODEC
	override fun canHarvestBlock(state : BlockState, level : BlockGetter, pos : BlockPos, player : Player) : Boolean =
		!player.isCreative

	override fun getStateForPlacement(context : BlockPlaceContext) : BlockState =
		this.defaultBlockState()
			.setValue(BlockStateProperties.HORIZONTAL_FACING, context.horizontalDirection.opposite)
			.setValue(BlockStateProperties.POWERED, false)

	override fun createBlockStateDefinition(builder : StateDefinition.Builder<Block, BlockState>) {
		builder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.POWERED)
	}

	override fun useWithoutItem(
		state : BlockState,
		level : Level,
		pos : BlockPos,
		player : Player,
		hitResult : BlockHitResult
	) : InteractionResult {
		if (!level.isClientSide) {
			val entity = level.getBlockEntity(pos) as? WheatCrusherBlockEntity ?: return InteractionResult.FAIL
			player.openMenu(entity, pos)
		}
		return InteractionResult.sidedSuccess(level.isClientSide)
	}

	override fun newBlockEntity(pos : BlockPos, state : BlockState) : BlockEntity =
		WheatCrusherBlockEntity(pos, state)

	override fun getRenderShape(state : BlockState) : RenderShape = RenderShape.MODEL
	override fun onRemove(
		state : BlockState,
		level : Level,
		pos : BlockPos,
		newState : BlockState,
		movedByPiston : Boolean
	) {
		if (!state.`is`(newState.block)) {
			val entity = (level.getBlockEntity(pos) as WheatCrusherBlockEntity)
			Containers.dropContents(level, pos, entity)
		}
		level.invalidateCapabilities(pos)
		super.onRemove(state, level, pos, newState, movedByPiston)
	}

	override fun <T : BlockEntity?> getTicker(
		level : Level,
		state : BlockState,
		blockEntityType : BlockEntityType<T>
	) : BlockEntityTicker<T>? = BaseEntityBlock.createTickerHelper(
		blockEntityType,
		ModBlockEntityTypes.WHEAT_CRUSHER.get()
	) { tLevel : Level, tPos : BlockPos, tState : BlockState, tBlockEntity : WheatCrusherBlockEntity ->
		tBlockEntity.tick(tLevel, tPos, tState)
	}
}
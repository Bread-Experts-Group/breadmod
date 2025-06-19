package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.CrossCollisionBlock
import net.minecraft.world.level.block.SimpleWaterloggedBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition.Builder
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.level.material.PushReaction.BLOCK
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.common.util.INBTSerializable
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.block.actual.entity.CableBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockStateProperties
import org.bread_experts_group.breadmod.util.Color

class CableBlock(
	private val capabilities: Set<BlockCapability<*, Direction?>>,
	private val capabilitiesConstructor: () -> List<INBTSerializable<*>>
) : BreadModBlockWithEntity(Properties.of().noOcclusion().pushReaction(BLOCK)), SimpleWaterloggedBlock {
	companion object {
		val directions: Map<Direction, Pair<BooleanProperty, VoxelShape>> = mapOf(
			Direction.UP to (ModBlockStateProperties.UP to box(5.0, 11.0, 5.0, 11.0, 16.0, 11.0)),
			Direction.DOWN to (ModBlockStateProperties.DOWN to box(5.0, 0.0, 5.0, 11.0, 5.0, 11.0)),
			Direction.NORTH to (ModBlockStateProperties.NORTH to box(5.0, 5.0, 0.0, 11.0, 11.0, 5.0)),
			Direction.SOUTH to (ModBlockStateProperties.SOUTH to box(5.0, 5.0, 11.0, 11.0, 11.0, 16.0)),
			Direction.EAST to (ModBlockStateProperties.EAST to box(11.0, 5.0, 5.0, 16.0, 11.0, 11.0)),
			Direction.WEST to (ModBlockStateProperties.WEST to box(0.0, 5.0, 5.0, 5.0, 11.0, 11.0)),
		)
		val CORE_SHAPE: VoxelShape = Block.box(5.0, 5.0, 5.0, 11.0, 11.0, 11.0)
	}

	init {
		this.registerDefaultState(
			this.defaultBlockState()
				.also { Companion.directions.forEach { (_, side) -> it.setValue(side.first, false) } }
				.setValue(BlockStateProperties.WATERLOGGED, false)
		)
	}

	fun resolveColor(): Int {
		val acceptable = this.capabilities.first()
		return when (acceptable) {
			Capabilities.EnergyStorage.BLOCK -> Color.RED
			Capabilities.ItemHandler.BLOCK   -> Color.GREEN
			Capabilities.FluidHandler.BLOCK  -> Color.BLUE
			else                             -> Color.WHITE
		}
	}

	fun connectsTo(
		neighborState: BlockState,
		neighborPos: BlockPos,
		neighborDirection: Direction,
		level: LevelAccessor
	): Boolean {
		if (neighborState.`is`(this)) return true
		return this.capabilities.any {
			(level as Level).getCapability(it, neighborPos, neighborDirection) != null
		}
	}

	override fun updateShape(
		state: BlockState,
		direction: Direction,
		neighborState: BlockState,
		level: LevelAccessor,
		pos: BlockPos,
		neighborPos: BlockPos
	): BlockState {
		if (state.getValue(CrossCollisionBlock.WATERLOGGED)) level.scheduleTick(
			pos,
			Fluids.WATER, Fluids.WATER.getTickDelay(level)
		)

		return state.setValue(
			Companion.directions.getValue(direction).first,
			this.connectsTo(
				neighborState,
				neighborPos,
				direction.opposite,
				level
			)
		)
	}

	override fun getStateForPlacement(context: BlockPlaceContext): BlockState = this.defaultBlockState().let {
		Companion.directions.forEach { (_, pair) -> it.setValue(pair.first, false) }
		var shape = it
		for (direction in Direction.entries) {
			val neighborPos = context.clickedPos.relative(direction)
			shape = this.updateShape(
				shape,
				direction,
				context.level.getBlockState(neighborPos),
				context.level,
				context.clickedPos,
				neighborPos
			)
		}
		val fluidState = context.level.getFluidState(context.clickedPos)
		shape.setValue(BlockStateProperties.WATERLOGGED, fluidState.type == Fluids.WATER)
	}

	override fun createBlockStateDefinition(builder: Builder<Block, BlockState>) {
		builder.add(*Companion.directions.values.map { it.first }.toTypedArray(), BlockStateProperties.WATERLOGGED)
	}

	override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
		var shape = Companion.CORE_SHAPE
		Companion.directions.forEach { (_, pair) ->
			if (state.getValue(pair.first)) shape = Shapes.or(shape, pair.second)
		}
		return shape
	}

	override fun hasDynamicShape(): Boolean = true

	override fun getBlockEntityType(level: Level, state: BlockState): BlockEntityType<*> =
		ModBlockEntityTypes.CABLE.get()
	override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = CableBlockEntity(
		pos, state,
		this.capabilities.zip(this.capabilitiesConstructor.invoke()).toMap()
	)

	override fun propagatesSkylightDown(state: BlockState, reader: BlockGetter, pos: BlockPos): Boolean =
		!state.getValue(BlockStateProperties.WATERLOGGED)

	override fun getFluidState(state: BlockState): FluidState =
		if (state.getValue(BlockStateProperties.WATERLOGGED)) Fluids.WATER.getSource(false)
		else super.getFluidState(state)
}
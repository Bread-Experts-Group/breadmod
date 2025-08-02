package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties.ENABLED
import net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import net.neoforged.neoforge.capabilities.Capabilities
import org.bread_experts_group.breadmod.client.render.entity.block.CreativeGeneratorRenderer
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.handler.ExtendedEnergyHandler
import org.bread_experts_group.breadmod.util.combine
import org.bread_experts_group.breadmod.util.rotate
import java.util.stream.Stream

class CreativeGeneratorBlock : BreadModBlock(
	Properties
		.ofFullCopy(Blocks.COPPER_BLOCK)
		.lightLevel { 6 }
) {
	companion object {
		val SHAPE_NORTH: VoxelShape = Stream.of(
			box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0),
			box(1.0, 1.0, 1.0, 15.0, 2.0, 15.0),
			box(2.0, 2.0, 2.0, 3.0, 3.0, 14.0),
			box(13.0, 2.0, 2.0, 14.0, 3.0, 14.0),
			box(3.0, 2.0, 2.0, 13.0, 3.0, 3.0),
			box(3.0, 2.0, 13.0, 13.0, 3.0, 14.0),
			box(1.0, 3.0, 15.0, 15.0, 4.0, 16.0),
			box(1.0, 12.0, 15.0, 15.0, 13.0, 16.0),
			box(4.0, 4.0, 15.0, 12.0, 12.0, 16.0),
			box(5.0, 3.0, 5.0, 11.0, 4.0, 11.0),
			box(4.0, 4.0, 4.0, 12.0, 12.0, 12.0),
			box(5.0, 12.0, 5.0, 11.0, 15.0, 11.0),
			box(5.0, 15.0, 5.0, 11.0, 16.0, 11.0),
			box(1.0, 15.0, 4.0, 15.0, 16.0, 5.0),
			box(1.0, 15.0, 11.0, 15.0, 16.0, 12.0),
			box(1.0, 15.0, 15.0, 15.0, 16.0, 16.0),
			box(1.0, 15.0, 0.0, 15.0, 16.0, 1.0),
			box(0.0, 15.0, 0.0, 1.0, 16.0, 16.0),
			box(15.0, 15.0, 0.0, 16.0, 16.0, 16.0),
			box(15.0, 12.0, 1.0, 16.0, 13.0, 15.0),
			box(0.0, 12.0, 1.0, 1.0, 13.0, 15.0),
			box(0.0, 3.0, 1.0, 1.0, 4.0, 15.0),
			box(15.0, 3.0, 1.0, 16.0, 4.0, 15.0),
			box(15.0, 4.0, 4.0, 16.0, 12.0, 12.0),
			box(0.0, 4.0, 4.0, 1.0, 12.0, 12.0),
			box(9.0, 1.0, 0.0, 15.0, 4.0, 1.0),
			box(0.0, 1.0, 0.0, 1.0, 15.0, 1.0),
			box(0.0, 1.0, 15.0, 1.0, 15.0, 16.0),
			box(15.0, 1.0, 15.0, 16.0, 15.0, 16.0),
			box(15.0, 1.0, 0.0, 16.0, 15.0, 1.0)
		).combine()
		val SHAPE_SOUTH: VoxelShape = this.SHAPE_NORTH.rotate(Rotation.CLOCKWISE_180)
		val SHAPE_EAST: VoxelShape = this.SHAPE_NORTH.rotate(Rotation.CLOCKWISE_90)
		val SHAPE_WEST: VoxelShape = this.SHAPE_NORTH.rotate(Rotation.COUNTERCLOCKWISE_90)
	}

	override fun shouldCreateEntity(with: Pair<BlockPos, BlockState>?): Boolean = true
	override val serverTickBM: BreadModTicker<ServerLevel> = serverTickBM@{ _, level, state, pos ->
		if (!state.getValue(ENABLED)) return@serverTickBM
		Direction.entries.forEach {
			val capability = level.getCapability(
				Capabilities.EnergyStorage.BLOCK,
				pos.relative(it), it.opposite
			)
			if (capability != null) {
				if (capability is ExtendedEnergyHandler) capability.receiveBigEnergy(capability.bigCapacity, false)
				else capability.receiveEnergy(Int.MAX_VALUE, false)
			}
		}
	}

	override fun ofRenderer(): ((BlockEntityRendererProvider.Context) -> BlockEntityRenderer<out BreadModBlockEntity>)? =
		::CreativeGeneratorRenderer

	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
		builder.add(HORIZONTAL_FACING, ENABLED)
	}

	override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
		this.defaultBlockState()
			.setValue(HORIZONTAL_FACING, context.horizontalDirection.opposite)
			.setValue(ENABLED, true)

	override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape =
		when (state.getValue(HORIZONTAL_FACING)) {
			Direction.NORTH -> Companion.SHAPE_NORTH
			Direction.SOUTH -> Companion.SHAPE_SOUTH
			Direction.EAST -> Companion.SHAPE_EAST
			Direction.WEST -> Companion.SHAPE_WEST
			else -> Shapes.block()
		}

	override fun animateTick(state: BlockState, level: Level, pos: BlockPos, random: RandomSource) {
		if (level !is ClientLevel) return
		if (state.getValue(ENABLED)) level.playLocalSound(
			pos, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS,
			1f, 1f, false
		)
	}

	override fun neighborChanged(
		state: BlockState,
		level: Level,
		pos: BlockPos,
		neighborBlock: Block,
		neighborPos: BlockPos,
		movedByPiston: Boolean
	) {
		if (level.hasNeighborSignal(pos) && state.getValue(ENABLED)) {
			level.playSound(null, pos, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1f, 1f)
			level.setBlockAndUpdate(pos, state.setValue(ENABLED, false))
			this.sendParticles(level, pos)
		} else if (!level.hasNeighborSignal(pos) && !state.getValue(ENABLED)) {
			level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1f, 1f)
			level.setBlockAndUpdate(pos, state.setValue(ENABLED, true))
			this.sendParticles(level, pos)
		}
	}

	private fun sendParticles(level: Level, pos: BlockPos) {
		val rand = (level.random.nextDouble() - 0.5) * 1.2
		val center = pos.center
		if (level is ServerLevel) level.sendParticles(
			ParticleTypes.END_ROD,
			center.x,
			center.y,
			center.z,
			10,
			rand,
			level.random.nextDouble(),
			rand,
			0.1
		)
	}
}
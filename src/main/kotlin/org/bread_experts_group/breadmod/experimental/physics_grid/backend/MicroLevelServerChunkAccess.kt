package org.bread_experts_group.breadmod.experimental.physics_grid.backend

import net.minecraft.core.BlockPos
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.LevelChunk
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.numeric.geometry.point.Point3

class MicroLevelServerChunkAccess(
	private val parent: ServerMicroLevel
) : LevelChunk(parent, ChunkPos.ZERO) {
	private val logger: Logger = LogManager.getLogger("ServerMicroLevel / ServerChunkAccess")
	val blocks: MutableMap<Point3<Int>, BlockState> = mutableMapOf()
	fun getBlockState(pos: Point3<Int>): BlockState = this.blocks[pos] ?: Blocks.AIR.defaultBlockState()
	override fun getBlockState(pos: BlockPos): BlockState = this.getBlockState(pos.toPoint3())

	private val tickingBlockEntities: MutableMap<Point3<Int>, MicroLevelBlockEntityTickerShell> = mutableMapOf()
	private fun <T : BlockEntity> updateBlockEntityTicker(blockEntity: T) {
		val state = blockEntity.blockState

		@Suppress("UNCHECKED_CAST")
		val ticker = state.getTicker(this.parent, blockEntity.type as BlockEntityType<T>)
		if (ticker == null) {
			this.logger.fatal("Remove ticker $blockEntity")
//			this.removeBlockEntityTicker(blockEntity.blockPos)
			return
		}
		val newTicker = MicroLevelBlockEntityTicker(this.parent, this, blockEntity, ticker)
		val posP3 = blockEntity.blockPos.toPoint3()
		val lastTicker = this.tickingBlockEntities[posP3]
		if (lastTicker != null) {
			lastTicker.ticker = newTicker
			return
		}
		val shell = MicroLevelBlockEntityTickerShell(newTicker)
		this.tickingBlockEntities[posP3] = shell
		this.parent.addBlockEntityTicker(shell)
	}

	override fun setBlockState(pos: BlockPos, state: BlockState, isMoving: Boolean): BlockState? {
		val oldState = this.blocks.put(pos.toPoint3(), state)
		if (oldState == state) return null
		// TODO: Light updates
		oldState?.onRemove(this.parent, pos, state, isMoving)
		state.onPlace(this.parent, pos, state, isMoving)
		if (state.hasBlockEntity()) {
			var oldEntity = this.getBlockEntity(pos, LevelChunk.EntityCreationType.CHECK)
			if (oldEntity != null && !oldEntity.isValidBlockState(state)) {
				this.removeBlockEntity(pos)
				oldEntity = null
			}

			if (oldEntity == null) {
				val newEntity = (state.block as EntityBlock).newBlockEntity(pos, state)
				if (newEntity != null) this.addAndRegisterBlockEntity(newEntity)
			} else {
				oldEntity.blockState = state
				this.updateBlockEntityTicker(oldEntity)
			}
		}
		return oldState
	}

	@Suppress("PROPERTY_HIDES_JAVA_FIELD")
	private val blockEntities: MutableMap<Point3<Int>, BlockEntity> = mutableMapOf()
	private fun createBlockEntity(pos: Point3<Int>): BlockEntity? {
		val state = this.getBlockState(pos)
		return if (state.hasBlockEntity()) (state.block as EntityBlock).newBlockEntity(pos.toBlockPos(), state)
		else null
	}

	fun getBlockEntity(pos: Point3<Int>, creationType: EntityCreationType): BlockEntity? {
		var blockEntity = this.blockEntities[pos]
		if (blockEntity != null && blockEntity.isRemoved) {
			this.blockEntities.remove(pos)
			blockEntity = null
		}
		return when (creationType) {
			EntityCreationType.CHECK -> blockEntity
			EntityCreationType.IMMEDIATE -> {
				blockEntity = this.createBlockEntity(pos)
				if (blockEntity != null) this.addAndRegisterBlockEntity(blockEntity)
				blockEntity
			}
			EntityCreationType.QUEUED -> throw IllegalStateException("QUEUED")
		}
	}

	override fun getBlockEntity(pos: BlockPos, creationType: EntityCreationType): BlockEntity? = this.getBlockEntity(
		pos.toPoint3(), creationType
	)

	override fun setBlockEntity(blockEntity: BlockEntity) {
		val position = blockEntity.blockPos.toPoint3()
		val state = this.getBlockState(position)
		if (!state.hasBlockEntity()) {
			this.logger.warn("Block state [$state] at $position has no block entity for $blockEntity")
			return
		}
		val blockEntityState = blockEntity.blockState
		if (blockEntityState != state) {
			if (!blockEntity.type.isValid(state)) {
				this.logger.warn("Block state [$state] is not consistent with $blockEntity at $position and isn't allowed")
				return
			}
			blockEntity.blockState = state
		}
		blockEntity.level = this.parent
		blockEntity.clearRemoved()
		val oldEntity = this.blockEntities.put(position, blockEntity)
		if (oldEntity != null) {
			oldEntity.setRemoved()
			this.logger.warn("Update aux light manager (auxLightManager.removeLightAt(blockpos);)")
		}
	}

	override fun addAndRegisterBlockEntity(blockEntity: BlockEntity) {
		this.setBlockEntity(blockEntity)
		this.addGameEventListener(blockEntity)
		this.updateBlockEntityTicker(blockEntity)
	}

	fun addGameEventListener(entity: BlockEntity) {
		val entityBlock = entity.blockState.block as? EntityBlock ?: return
		val gameEventListener = entityBlock.getListener(this.parent, entity) ?: return
		this.getListenerRegistry(0).register(gameEventListener)
		// TODO: SectionPos.blockToSectionCoord(blockEntity.getBlockPos().getY()) ?
	}
}
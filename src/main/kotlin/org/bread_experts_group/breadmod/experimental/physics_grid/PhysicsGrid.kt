package org.bread_experts_group.breadmod.experimental.physics_grid

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.VoxelShape
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.client.ClientMicroLevelChunk
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.server.ServerMicroLevel
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.server.ServerMicroLevelChunk
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.toBlockPos
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.toVec3
import org.bread_experts_group.breadmod.network.clientbound.physics_grid.NewPhysicsGridPacket
import org.bread_experts_group.breadmod.util.component1
import org.bread_experts_group.breadmod.util.component2
import org.bread_experts_group.breadmod.util.component3
import org.bread_experts_group.breadmod.util.logDebugInfo
import org.bread_experts_group.breadmod.util.minus
import org.bread_experts_group.breadmod.util.plus
import org.bread_experts_group.breadmod.util.rayCast
import org.bread_experts_group.breadmod.util.toVec3
import org.bread_experts_group.breadmod.util.toVec3i

class PhysicsGrid(
	val id: Long,
	var pos: Vec3,
	var bounding: AABB,
	val level: Level
) {
	companion object {
		@JvmField
		val serverGrids: MutableMap<Long, PhysicsGrid> = mutableMapOf()

		@JvmField
		val clientGrids: MutableMap<Long, PhysicsGrid> = mutableMapOf()

		@JvmStatic
		fun getClosestGrid(entity: Entity): PhysicsGrid? {
			val gridDist = if (entity.level().isClientSide) this.clientGrids else this.serverGrids
			val collide = gridDist.values.firstOrNull { entity.boundingBox.intersects(it.bounding) }
			if (collide != null) return collide
			return entity.rayCast(50.0, { _, _, to ->
				gridDist.values.firstOrNull { it.bounding.contains(to) }
			})?.hit
		}

		@JvmStatic
		fun redirectLevelToGrid(original: Level?): Level? {
			val player = localClient.player ?: return original
			val grid = this.getClosestGrid(player)
			return grid?.microLevel
		}

		fun collectBlocksAndEntities(
			posA: BlockPos,
			posB: BlockPos,
			level: Level
		): Pair<Map<BlockPos, BlockState>, Map<BlockPos, BlockEntity>> {
			val blocks: MutableMap<BlockPos, BlockState> = mutableMapOf()
			val blockEntities: MutableMap<BlockPos, BlockEntity> = mutableMapOf()
			BlockPos.betweenClosedStream(posA, posB).forEach { pos ->
				val immutable = pos.immutable()
				val state = level.getBlockState(immutable)
				if (state.isAir) return@forEach
				val posOffset = BlockPos(immutable.x - posA.x, immutable.y - posA.y, immutable.z - posA.z)
				blocks[posOffset] = state
				val blockEntity = level.getBlockEntity(immutable)
				if (blockEntity != null && state.block is EntityBlock) {
					val data = blockEntity.saveWithId(level.registryAccess())
					val newEntity = (state.block as EntityBlock).newBlockEntity(posOffset, state)
					if (newEntity != null) {
						newEntity.loadWithComponents(data, level.registryAccess())
						blockEntities[posOffset] = newEntity
					}
				}
			}
			return blocks to blockEntities
		}

		private var nextID: Long = 0L
		fun add(posA: BlockPos, posB: BlockPos, context: UseOnContext) {
			val level = (context.level as? ServerLevel) ?: return
			val targetPos = context.clickedPos.relative(context.clickedFace).toVec3()
			val (blocks, blockEntities) = this.collectBlocksAndEntities(posA, posB, level)
			val bounding = AABB.of(BoundingBox.fromCorners(posA, posB)).move(targetPos - posA.toVec3())
			logDebugInfo(bounding)
			val id = this.nextID++
			val grid = PhysicsGrid(id, targetPos, bounding, level)
			grid.microLevel = ServerMicroLevel(grid, level)
			blocks.forEach { (pos, state) -> grid.microLevel.setBlock(pos, state, 0) }
			blockEntities.forEach { (_, blockEntity) -> grid.microLevel.setBlockEntity(blockEntity) }
			Companion.serverGrids[id] = grid
			PacketDistributor.sendToPlayersInDimension(
				level,
				NewPhysicsGridPacket(id, targetPos, bounding, posA, posB),
			)
		}
	}

	var delta: Vec3 = Vec3.ZERO
	var oldPos: Vec3 = this.pos
	lateinit var microLevel: Level
	val playersInGrid: ArrayList<ServerPlayer> = arrayListOf()
	private val blockFilter: List<Block> = listOf(Blocks.AIR, Blocks.VOID_AIR, Blocks.CAVE_AIR, Blocks.LIGHT)
	fun gridBlockCast(entity: Entity, hitDistance: Double): GridHitResult? {
		val cast = entity.rayCast<Triple<Vec3, Pair<Direction, BlockState>, BlockPos>>(hitDistance, { _, from, to ->
			val relativeFrom = from - this.pos
			val relativeTo = to - this.pos
			val blockPos = BlockPos(relativeTo.toVec3i())
			val found = this.microLevel.getBlockState(blockPos)
			if (found != null) {
				val shape = found.getShape(this.microLevel, blockPos)
				val clip = shape.clip(relativeFrom, relativeTo, blockPos) ?: return@rayCast null
				if (found.block !in this.blockFilter) Triple(relativeTo, clip.direction to found, blockPos) else null
			} else null
		}) ?: return null
		val (localVec, pair, localPos) = cast.hit
		return GridHitResult(localVec, pair.first, localPos, pair.second)
	}

	fun gridBlockCast(player: Player): GridHitResult? {
		val attribute = (player.attributes.getInstance(Attributes.ENTITY_INTERACTION_RANGE) ?: return null).value
		return this.gridBlockCast(player, attribute)
	}

	fun getPosLerped(partialTick: Float): Vec3 {
		val delta = if (this.delta == Vec3.ZERO) 1.0 else partialTick.toDouble()
		return this.oldPos.lerp(this.pos, delta)
	}

	fun movementTick() {
		if (this.delta == BlockPos.ZERO) return
		this.oldPos = this.pos
		this.pos += this.delta
		this.bounding = this.bounding.move(this.delta)
		val entities = this.level.getEntities(null, this.bounding)
		entities.forEach { entity ->
			/*if (entity.onGround())*/ entity.setPos(entity.position() + this.delta)
		}
		val x = this.delta.x + if (this.delta.x < 0) ((-this.delta.x) / 10) else -(this.delta.x / 10)
		val y = this.delta.y + if (this.delta.y < 0) ((-this.delta.y) / 10) else -(this.delta.y / 10)
		val z = this.delta.z + if (this.delta.z < 0) ((-this.delta.z) / 10) else -(this.delta.z / 10)
		this.delta = Vec3(x, y, z)
	}

	fun tick(server: MinecraftServer) {
		if (this.microLevel.isClientSide) return
		server.playerList.players.forEach { player ->
			val intersects = player.boundingBox.intersects(this.bounding)
			if (intersects && !this.playersInGrid.contains(player)) this.playersInGrid.add(player)
			else this.playersInGrid.removeIf { !intersects }
		}
		(this.microLevel as ServerLevel).tick { true }
	}

	fun getNearbyShapes(isClient: Boolean, position: Vec3): List<VoxelShape> {
		val chunk = this.microLevel.getChunk(0, 0)
		val blocks = if (isClient) (chunk as ClientMicroLevelChunk).blocks
		else (chunk as ServerMicroLevelChunk).blocks
		val nearbyBlocks = blocks.filter { (blockPos, _) ->
			this.pos.add(blockPos.toVec3()).distanceTo(position) < 5.0
		}
		return buildList {
			nearbyBlocks.forEach { (pos, state) ->
				val (x, y, z) = this@PhysicsGrid.pos.add(pos.toVec3())
				if (state.getCollisionShape(this@PhysicsGrid.microLevel, pos.toBlockPos()).isEmpty) return@forEach
				this.add(state.getShape(this@PhysicsGrid.microLevel, pos.toBlockPos()).move(x, y, z))
			}
		}
	}

	fun getNearbyShapes(entity: Entity): List<VoxelShape> =
		this.getNearbyShapes(entity.level().isClientSide, entity.position())
}
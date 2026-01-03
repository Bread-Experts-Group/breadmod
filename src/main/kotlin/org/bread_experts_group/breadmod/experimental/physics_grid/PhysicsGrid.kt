package org.bread_experts_group.breadmod.experimental.physics_grid

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.VertexBuffer
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.RenderType
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
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.client.render.buffer.RenderBuffer
import org.bread_experts_group.breadmod.client.render.initialTranslate
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.offsetRenderToCameraPos
import org.bread_experts_group.breadmod.client.render.translate
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.server.ServerMicroLevel
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.server.ServerMicroLevelChunkAccess
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.toBlockPos
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.toVec3
import org.bread_experts_group.breadmod.experimental.physics_grid.render.GridMesh
import org.bread_experts_group.breadmod.network.clientbound.physics_grid.NewPhysicsGridPacket
import org.bread_experts_group.breadmod.util.component1
import org.bread_experts_group.breadmod.util.component2
import org.bread_experts_group.breadmod.util.component3
import org.bread_experts_group.breadmod.util.logDebugInfo
import org.bread_experts_group.breadmod.util.minus
import org.bread_experts_group.breadmod.util.rayCast
import org.bread_experts_group.breadmod.util.toVec3
import org.bread_experts_group.breadmod.util.toVec3i

class PhysicsGrid(val pos: Vec3, val bounding: AABB) {
	companion object {
		val gridMeshes: MutableMap<PhysicsGrid, GridMesh> = mutableMapOf()

		@JvmField
		val localGrids: MutableList<PhysicsGrid> = mutableListOf()

		@JvmStatic
		fun getClosestGrid(entity: Entity): PhysicsGrid? = this.localGrids.firstOrNull {
			entity.boundingBox.intersects(it.bounding)
		}

		fun add(posA: BlockPos, posB: BlockPos, context: UseOnContext) {
			val level = (context.level as? ServerLevel) ?: return
			val targetPos = context.clickedPos.relative(context.clickedFace).toVec3()
			val blocks: MutableMap<BlockPos, BlockState> = mutableMapOf()
			val blockEntities: MutableMap<BlockPos, BlockEntity> = mutableMapOf()
			val bounding = AABB.of(BoundingBox.fromCorners(posA, posB)).move(targetPos - posA.toVec3())
			logDebugInfo(bounding)
			BlockPos.betweenClosedStream(posA, posB).forEach { pos ->
				val immutable = pos.immutable()
				val state = level.getBlockState(immutable)
				if (state.isAir) return@forEach
				val posOffset = BlockPos(immutable.x - posA.x, immutable.y - posA.y, immutable.z - posA.z)
				val blockEntity = level.getBlockEntity(immutable)
				if (blockEntity != null && state.block is EntityBlock) {
					val data = blockEntity.saveWithId(level.registryAccess())
					val newEntity = (state.block as EntityBlock).newBlockEntity(posOffset, state)
					if (newEntity != null) {
						newEntity.loadWithComponents(data, level.registryAccess())
						blockEntities[posOffset] = newEntity
					}
				}
				blocks[posOffset] = state
			}
			val grid = PhysicsGrid(targetPos, bounding)
			grid.microLevel = ServerMicroLevel(grid, level)
			blocks.forEach { (pos, state) -> grid.microLevel.setBlock(pos, state, 0) }
			blockEntities.forEach { (_, blockEntity) -> grid.microLevel.setBlockEntity(blockEntity) }
			Companion.localGrids.add(grid)
			PacketDistributor.sendToPlayersInDimension(
				level,
				NewPhysicsGridPacket(targetPos, bounding),
			)
		}
	}

	lateinit var microLevel: Level
	val playersInGrid: ArrayList<ServerPlayer> = arrayListOf()
	private val blockFilter: List<Block> = listOf(Blocks.AIR, Blocks.VOID_AIR, Blocks.CAVE_AIR, Blocks.LIGHT)
	fun gridBlockCast(entity: Entity, hitDistance: Double): GridHitResult? {
		val cast = entity.rayCast<Triple<Vec3, Pair<Direction, BlockState>, BlockPos>>(hitDistance) { _, from, to ->
			val relativeFrom = from - this.pos
			val relativeTo = to - this.pos
			val blockPos = BlockPos(relativeTo.toVec3i())
			val found = this.microLevel.getBlockState(blockPos)
			if (found != null) {
				val shape = found.getShape(this.microLevel, blockPos)
				val clip = shape.clip(relativeFrom, relativeTo, blockPos) ?: return@rayCast null
				if (found.block !in this.blockFilter) Triple(relativeTo, clip.direction to found, blockPos) else null
			} else null
		} ?: return null
		val (localVec, pair, localPos) = cast.hit
		return GridHitResult(localVec, pair.first, localPos, pair.second)
	}

	fun gridBlockCast(player: Player): GridHitResult? {
		val attribute = (player.attributes.getInstance(Attributes.ENTITY_INTERACTION_RANGE) ?: return null).value
		return this.gridBlockCast(player, attribute)
	}

	fun tick(server: MinecraftServer) {
		if (this.microLevel is ClientLevel) return
		server.playerList.players.forEach { player ->
			val intersects = player.boundingBox.intersects(this.bounding)
			if (intersects && !this.playersInGrid.contains(player)) this.playersInGrid.add(player)
			else this.playersInGrid.removeIf { !intersects }
		}
		(this.microLevel as ServerLevel).tick { true }
	}

	fun attachRenderer() {
		RenderBuffer.add(RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS, { event, _ ->
			val bufferSource = localClient.renderBuffers().bufferSource()
			val gridMesh = Companion.gridMeshes.getOrPut(this) { GridMesh(this) }
			val poseStack = event.poseStack
			gridMesh.compile(poseStack)
			val shaderInstance = RenderSystem.getShader() ?: return@add true
			gridMesh.getBuffers().forEach { buffer ->
				poseStack.pushPose()
				poseStack.mulPose(event.modelViewMatrix)
				poseStack.offsetRenderToCameraPos(this.pos, event.camera, false)
				buffer.bind()
				buffer.drawWithShader(
					poseStack.last().pose(),
					event.projectionMatrix,
					shaderInstance
				)
				shaderInstance.clear()
				VertexBuffer.unbind()
				poseStack.popPose()
			}

			poseStack.pushPose()
			poseStack.initialTranslate(event.camera)
			LevelRenderer.renderLineBox(
				poseStack,
				bufferSource.getBuffer(RenderType.lines()),
				this.bounding,
				1f,
				1f,
				1f,
				1f
			)
			poseStack.translate(this.pos)
			// TODO: Client level blocks
//			(this.microLevel.getChunk(0, 0) as ServerMicroLevelChunkAccess).blocks.forEach { (pos, _) ->
//				val blockEntity = this.microLevel.getBlockEntity(pos.toBlockPos()) ?: return@forEach
//				poseStack.pushPose()
//				poseStack.translate(pos.toBlockPos())
//				val renderer = localClient.blockEntityRenderDispatcher.getRenderer(blockEntity)
//				renderer?.render(
//					blockEntity,
//					1f,
//					poseStack,
//					bufferSource,
//					LightTexture.FULL_BRIGHT,
//					OverlayTexture.NO_OVERLAY
//				)
//				poseStack.popPose()
//			}
			poseStack.popPose()
			if (!Companion.localGrids.contains(this)) {
				gridMesh.close()
				Companion.gridMeshes.remove(this)
				true
			} else false
		})
	}

	fun getNearbyShapes(entity: Entity): List<VoxelShape> {
		// TODO: client micro level
		return emptyList()
//		val nearbyBlocks = (this.microLevel.getChunk(0, 0) as ServerMicroLevelChunkAccess).blocks
//			.filter { (blockPos, _) -> this.pos.add(blockPos.toVec3()).distanceTo(entity.position()) < 5.0 }
//		return buildList {
//			nearbyBlocks.forEach { (pos, state) ->
//				val (x, y, z) = this@PhysicsGrid.pos.add(pos.toVec3())
//				if (state.getCollisionShape(this@PhysicsGrid.microLevel, pos.toBlockPos()).isEmpty) return@forEach
//				this.add(state.getShape(this@PhysicsGrid.microLevel, pos.toBlockPos()).move(x, y, z))
//			}
//		}
	}

	fun getNearbyShapesAndPos(entity: Entity): List<Pair<BlockPos, VoxelShape>> {
		val nearbyBlocks = (this.microLevel.getChunk(0, 0) as ServerMicroLevelChunkAccess).blocks
			.filter { (pos, _) -> this.pos.add(pos.toVec3()).distanceTo(entity.position()) < 5.0 }
		return buildList {
			nearbyBlocks.forEach { (pos, state) ->
				val (x, y, z) = this@PhysicsGrid.pos.add(pos.toVec3())
				this.add(
					pos.toBlockPos() to
							state.getShape(this@PhysicsGrid.microLevel, pos.toBlockPos())
								.move(x, y, z)
				)
			}
		}
	}
}
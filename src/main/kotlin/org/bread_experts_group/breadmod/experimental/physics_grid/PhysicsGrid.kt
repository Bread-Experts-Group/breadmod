package org.bread_experts_group.breadmod.experimental.physics_grid

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.VertexBuffer
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.VoxelShape
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import org.bread_experts_group.breadmod.client.render.buffer.RenderBuffer
import org.bread_experts_group.breadmod.client.render.initialTranslate
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.translate
import org.bread_experts_group.breadmod.experimental.physics_grid.render.GridMesh
import org.bread_experts_group.breadmod.util.component1
import org.bread_experts_group.breadmod.util.component2
import org.bread_experts_group.breadmod.util.component3
import org.bread_experts_group.breadmod.util.logDebugInfo
import org.bread_experts_group.breadmod.util.minus
import org.bread_experts_group.breadmod.util.rayCast
import org.bread_experts_group.breadmod.util.toVec3
import org.bread_experts_group.breadmod.util.toVec3i

class PhysicsGrid private constructor(
	val microLevel: ServerMicroLevel,
	val pos: Vec3,
	val center: Vec3,
	val bounding: AABB
) {
	companion object {
		val gridMeshes: MutableMap<PhysicsGrid, GridMesh> = mutableMapOf()
		val grids: MutableList<PhysicsGrid> = mutableListOf()

		fun getClosestGrid(entity: Entity): PhysicsGrid? =
			this.grids.firstOrNull { entity.boundingBox.intersects(it.bounding) }

		fun add(posA: BlockPos, posB: BlockPos, context: UseOnContext, level: Level) {
			val targetPos = context.clickedPos.relative(context.clickedFace).toVec3().add(0.5, 0.5, 0.5)
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
				if (blockEntity != null) blockEntities[posOffset] = blockEntity
				blocks[posOffset] = state
			}
			Companion.grids.add(
				PhysicsGrid(
					ServerMicroLevel(blocks, blockEntities),
					targetPos,
					bounding.center,
					bounding
				)
			)
		}
	}

	init {
		val player = localClient.player!!
		player.sendSystemMessage(Component.literal("blocks: ${this.microLevel.blocks.size}"))
		this.attachRenderer()
	}

	private val blockFilter: List<Block> = listOf(Blocks.AIR, Blocks.VOID_AIR, Blocks.CAVE_AIR, Blocks.LIGHT)
	fun gridBlockCast(entity: Entity, hitDistance: Double): GridHitResult? {
		val cast = entity.rayCast<Triple<Vec3, Pair<Direction, BlockState>, BlockPos>>(hitDistance) { _, from, to ->
			val relativeFrom = from - this.pos
			val relativeTo = to - this.pos
			val blockPos = BlockPos(relativeTo.toVec3i())
			val found = this.microLevel.blocks[blockPos]
			if (found != null) {
				val shape = found.getShape(this.microLevel, blockPos)
				val clip = shape.clip(relativeFrom, relativeTo, blockPos) ?: return@rayCast null
				if (found.block !in this.blockFilter) Triple(relativeTo, clip.direction to found, blockPos) else null
			} else null
		} ?: return null
		val (localVec, pair, localPos) = cast.hit
		return GridHitResult(localVec, pair.first, localPos, pair.second)
	}

	fun attachRenderer() {
		RenderBuffer.add(RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS, { event, _ ->
			val gridMesh = Companion.gridMeshes.getOrPut(this) { GridMesh(this) }
			val poseStack = event.poseStack
			gridMesh.compile(poseStack)
			val shaderInstance = RenderSystem.getShader() ?: return@add true
			val (x, y, z) = event.camera.position
			poseStack.pushPose()
			poseStack.mulPose(event.modelViewMatrix)
			poseStack.translate(-x, -y, -z)
			poseStack.translate(this.pos)
			gridMesh.getBuffers().forEach { buffer ->
				buffer.bind()
				buffer.drawWithShader(
					poseStack.last().pose(),
					event.projectionMatrix,
					shaderInstance
				)
				shaderInstance.clear()
				VertexBuffer.unbind()
			}
			poseStack.popPose()

			poseStack.pushPose()
			poseStack.initialTranslate(event.camera)
			LevelRenderer.renderLineBox(
				poseStack,
				localClient.renderBuffers().bufferSource().getBuffer(RenderType.lines()),
				this.bounding,
				1f,
				1f,
				1f,
				1f
			)
			poseStack.translate(this.pos)
			this.microLevel.blockEntities.forEach { (pos, blockEntity) ->
				poseStack.pushPose()
				poseStack.translate(pos)
				val renderer = localClient.blockEntityRenderDispatcher.getRenderer(blockEntity)
				renderer?.render(
					blockEntity,
					1f,
					poseStack,
					localClient.renderBuffers().bufferSource(),
					LightTexture.FULL_BRIGHT,
					OverlayTexture.NO_OVERLAY
				)
				poseStack.popPose()
			}
			if (!Companion.grids.contains(this)) {
				gridMesh.close()
				Companion.gridMeshes.remove(this)
				true
			} else false
		})
	}

	fun getNearbyShapes(entity: Entity): List<VoxelShape> {
		val nearbyBlocks =
			this.microLevel.blocks.filter { this.pos.add(it.component1().toVec3()).distanceTo(entity.position()) < 5.0 }
		return buildList {
			nearbyBlocks.forEach { (pos, state) ->
				val (x, y, z) = this@PhysicsGrid.pos.add(pos.toVec3())
				this.add(state.getShape(this@PhysicsGrid.microLevel, pos).move(x, y, z))
			}
		}
	}

	fun getNearbyShapesAndPos(entity: Entity): List<Pair<BlockPos, VoxelShape>> {
		val nearbyBlocks =
			this.microLevel.blocks.filter { (pos, _) -> this.pos.add(pos.toVec3()).distanceTo(entity.position()) < 5.0 }
		return buildList {
			nearbyBlocks.forEach { (pos, state) ->
				val (x, y, z) = this@PhysicsGrid.pos.add(pos.toVec3())
				this.add(pos to state.getShape(this@PhysicsGrid.microLevel, pos).move(x, y, z))
			}
		}
	}
}
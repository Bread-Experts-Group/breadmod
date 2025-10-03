package org.bread_experts_group.breadmod.experimental.physics_grid

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.VertexBuffer
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
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
import org.bread_experts_group.breadmod.util.div
import org.bread_experts_group.breadmod.util.logDebugInfo
import org.bread_experts_group.breadmod.util.minus
import org.bread_experts_group.breadmod.util.plus
import org.bread_experts_group.breadmod.util.times
import org.bread_experts_group.breadmod.util.toVec3

class PhysicsGrid private constructor(
	val level: Level,
	val blocks: Map<BlockPos, Pair<VoxelShape, BlockState>>,
	val blockEntities: Map<BlockPos, BlockEntity>,
	val pos: Vec3,
	val center: Vec3,
	val bounding: AABB
) {
	val random: RandomSource = RandomSource.create(42)

	companion object {
		val gridMeshes: MutableMap<PhysicsGrid, GridMesh> = mutableMapOf()
		val grids: MutableList<PhysicsGrid> = mutableListOf()
		fun add(posA: BlockPos, posB: BlockPos, context: UseOnContext, level: Level) {
			val targetPos = context.clickedPos.relative(context.clickedFace).toVec3()
			val a = posA
			val b = posB
			val blocks: MutableMap<BlockPos, Pair<VoxelShape, BlockState>> = mutableMapOf()
			val blockEntities: MutableMap<BlockPos, BlockEntity> = mutableMapOf()
			val center = ((a.center / 2.0) - (b.center / 2.0)).minus(0.5, 0.5, 0.5)
			val bounding = AABB(
				0.0,
				0.0,
				0.0,
				a.x - b.x - 1.0,
				a.y - b.y - 1.0,
				a.z - b.z - 1.0
			).move(targetPos.minus(center.times(2.0)))
			val centerOffset = a.toVec3() + center
			logDebugInfo(bounding)
			BlockPos.betweenClosedStream(a, b).forEach { pos ->
				val immutable = pos.immutable()
				val state = level.getBlockState(immutable)
				if (state.isAir) return@forEach
				val posOffset = BlockPos(immutable.x - a.x, immutable.y - a.y, immutable.z - a.z)
				val blockEntity = level.getBlockEntity(immutable)
				if (blockEntity != null) blockEntities[posOffset] = blockEntity
				blocks[posOffset] = state.getShape(level, immutable) to state
			}
			Companion.grids.add(PhysicsGrid(level, blocks, blockEntities, targetPos, center, bounding))
		}
	}

	init {
		val player = localClient.player!!
		player.sendSystemMessage(Component.literal("blocks: ${this.blocks.size}"))
		this.attachRenderer()
	}

	fun attachRenderer() {
		RenderBuffer.add(RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS, { event, _ ->
			val gridMesh = Companion.gridMeshes.getOrPut(this) { GridMesh(this) }
			val poseStack = event.poseStack
			gridMesh.compile(poseStack)
			val shaderInstance = RenderSystem.getShader() ?: return@add true
			val (x, y, z) = event.camera.position
			poseStack.pushPose()
			poseStack.mulPose(event.modelViewMatrix)
			poseStack.translate(-x, -y, -z)
			poseStack.translate(this.pos)
			gridMesh.vertexBuffers.values.forEach { buffer ->
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
			poseStack.translate(this.pos)
			this.blockEntities.forEach { (pos, blockEntity) ->
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
			this.blocks.filter { this.pos.add(it.component1().toVec3()).distanceTo(entity.position()) < 5.0 }
		return buildList {
			nearbyBlocks.forEach { (pos, pair) ->
				val (x, y, z) = this@PhysicsGrid.pos.add(pos.toVec3())
				this.add(pair.first.move(x, y, z))
			}
		}
	}
}
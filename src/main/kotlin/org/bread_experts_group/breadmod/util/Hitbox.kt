package org.bread_experts_group.breadmod.util

import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.debug.DebugRenderer
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import org.bread_experts_group.breadmod.client.render.offsetRenderToCameraPos
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity

typealias HitboxParams<T> = (T, BlockPos, BlockState, Player, BreadModBlockEntity) -> Unit

class Hitbox(
	var bounds: AABB,
	val originBlockPos: BlockPos,
	val pos: Vec3,
	val onHit: HitboxParams<Level> = { _, _, _, _, _ -> },
) {
	constructor(
		size: Double,
		originBlockPos: BlockPos,
		pos: Vec3,
		onHitCommon: HitboxParams<Level> = { _, _, _, _, _ -> },
	) : this(AABB.ofSize(Vec3.ZERO, size, size, size), originBlockPos, pos, onHitCommon)

	fun render(event: RenderLevelStageEvent, bufferSource: MultiBufferSource) {
		if (event.stage != RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS) return
		val poseStack = event.poseStack

		poseStack.pushPose()
		poseStack.offsetRenderToCameraPos(this.pos, event.camera, false)
		DebugRenderer.renderFilledBox(
			poseStack,
			bufferSource,
			this.bounds,
			0.9f,
			0.5f,
			0.5f,
			0.6f
		)
		poseStack.popPose()

//		DebugRenderer.renderFloatingText(
//			poseStack,
//			bufferSource,
//			this.pos.toString(),
//			this.pos.x,
//			this.pos.y + 1.0,
//			this.pos.z,
//			Color.WHITE
//		)
	}

	fun resize(newSize: Double) {
		this.bounds = AABB.ofSize(Vec3.ZERO, newSize, newSize, newSize)
	}

	fun move(x: Double, y: Double, z: Double) {
		this.pos.add(x, y, z)
	}

}
package org.bread_experts_group.breadmod.client.render.entity

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.client.model.data.ModelData
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.translate
import org.bread_experts_group.breadmod.experimental.fake_level.FakeLevel
import org.bread_experts_group.breadmod.registry.entity.actual.Rocket
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.unaryMinus

class RocketRenderer(
	private val context: EntityRendererProvider.Context
) : EntityRenderer<Rocket>(context) {
	override fun getTextureLocation(entity: Rocket): ResourceLocation = modLocation()
	private var fakeLevel: FakeLevel? = null

	override fun render(
		entity: Rocket,
		entityYaw: Float,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedLight: Int
	) {
		val blocks = entity.entityData.get(Rocket.BLOCKS)
		val origin = entity.entityData.get(Rocket.ORIGIN)
		val center = entity.entityData.get(Rocket.BOTTOM_CENTER)
		if (this.fakeLevel == null) this.fakeLevel =
			FakeLevel(entity.level(), -64, 64)

		this.fakeLevel?.let { fakeLevel ->
			if (fakeLevel.blockStates.isEmpty()) {
				blocks.forEach { (pos, state) ->
					fakeLevel.setBlock(pos, state)
				}
			}

			fakeLevel.blockStates.forEach { (pos, state) ->
				val offset = pos.offset(-origin)
				poseStack.pushPose()
				poseStack.translate(offset.toVec3())
				poseStack.translate(-0.5, 0.0, -0.5)
				poseStack.translate(center.toVec3())
				val renderTypes = this.context.blockRenderDispatcher.getBlockModel(state)
					.getRenderTypes(state, entity.random, ModelData.EMPTY)
				renderTypes.forEach {
					this.context.blockRenderDispatcher.renderBatched(
						state,
						pos,
						fakeLevel,
						poseStack,
						bufferSource.getBuffer(it),
						true,
						entity.random
					)
				}
				poseStack.popPose()
			}
			fakeLevel.clear()
		}
//		LogManager.getLogger().info(center)
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight)
	}
}
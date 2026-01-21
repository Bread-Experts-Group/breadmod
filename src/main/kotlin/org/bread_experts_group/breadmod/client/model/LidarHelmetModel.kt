package org.bread_experts_group.breadmod.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.model.Model
import net.minecraft.client.model.geom.EntityModelSet
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.localClient

class LidarHelmetModel(private val modelSet: EntityModelSet) : Model(RenderType::entitySolid) {
	companion object {
		val HELMET_LAYER: ModelLayerLocation = ModelLayerLocation(modLocation("lidar_helmet"), "main")
		val HELMET_TEXTURE: ResourceLocation = modLocation("textures/models/armor/lidar_helmet.png")
		fun createLayerDefinition(): LayerDefinition =
			LayerDefinition.create(this.createMesh(), 64, 64)

		private fun createMesh(): MeshDefinition {
			val meshDefinition = MeshDefinition()
			val partDefinition = meshDefinition.root

			partDefinition.addOrReplaceChild(
				"lidar_helmet",
				CubeListBuilder.create()
					.texOffs(0, 8).addBox(-5.0f, -1.0f, -3.0f, 1.0f, 2.0f, 10.0f, CubeDeformation.NONE)
					.texOffs(0, 20).addBox(4.0f, -1.0f, -3.0f, 1.0f, 2.0f, 10.0f, CubeDeformation.NONE)
					.texOffs(22, 8).addBox(-4.0f, -1.0f, 6.0f, 8.0f, 2.0f, 1.0f, CubeDeformation.NONE)
					.texOffs(0, 0).addBox(-6.0f, -2.0f, -7.0f, 12.0f, 4.0f, 4.0f, CubeDeformation.NONE),
				PartPose.ZERO
			)

			return meshDefinition
		}
	}

	private val parts: List<ModelPart> = this.modelSet.bakeLayer(Companion.HELMET_LAYER).allParts.toList()

	override fun renderToBuffer(
		poseStack: PoseStack,
		buffer: VertexConsumer,
		packedLight: Int,
		packedOverlay: Int,
		color: Int
	): Unit = this.parts.forEach { it.render(poseStack, buffer, packedLight, packedOverlay, color) }

	fun render(
		poseStack: PoseStack,
		packedLight: Int,
		packedOverlay: Int,
		color: Int
	): Unit = this.renderToBuffer(
		poseStack,
		localClient.renderBuffers().bufferSource().getBuffer(this.renderType(Companion.HELMET_TEXTURE)),
		packedLight,
		packedOverlay,
		color
	)
}
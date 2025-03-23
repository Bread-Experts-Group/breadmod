package org.bread_experts_group.breadmod.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.model.Model
import net.minecraft.client.model.geom.EntityModelSet
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.client.model.geom.builders.PartDefinition
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.localClient

class GluonGunBackpackModel(private val modelSet: EntityModelSet) : Model(RenderType::entitySolid) {
	private val parts = this.modelSet.bakeLayer(Companion.BACKPACK_LAYER).allParts.toList()

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
		localClient.renderBuffers().bufferSource().getBuffer(this.renderType(Companion.BACKPACK_TEXTURE)),
		packedLight,
		packedOverlay,
		color
	)

	companion object {
		val BACKPACK_LAYER: ModelLayerLocation = ModelLayerLocation(modLocation("gluon_gun_backpack"), "main")
		val BACKPACK_TEXTURE: ResourceLocation = modLocation("textures/item/gluon_gun_backpack.png")
		fun createLayerDefinition(): LayerDefinition = LayerDefinition.create(this.createMesh(), 128, 128)
		private fun createMesh(): MeshDefinition {
			val meshDefinition = MeshDefinition()
			val partDefinition = meshDefinition.root
			val backpack: PartDefinition = partDefinition.addOrReplaceChild(
				"Backpack",
				CubeListBuilder.create().texOffs(0, 0)
					.addBox(-8.0f, -9.4673f, 0.6748f, 16.0f, 22.0f, 6.0f, CubeDeformation(0.0f))
					.texOffs(0, 28).addBox(-9.0f, -9.4673f, -5.3252f, 7.0f, 22.0f, 6.0f, CubeDeformation(0.0f))
					.texOffs(64, 0).addBox(-2.0f, 0.7327f, -5.3252f, 4.0f, 10.0f, 6.0f, CubeDeformation(0.0f))
					.texOffs(50, 0).addBox(-2.0f, 10.5327f, -1.3252f, 4.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
					.texOffs(26, 28).addBox(2.0f, -9.4673f, -5.3252f, 7.0f, 22.0f, 6.0f, CubeDeformation(0.0f))
					.texOffs(84, 0).addBox(-7.0f, -10.4673f, 0.6748f, 14.0f, 1.0f, 6.0f, CubeDeformation(0.0f)),
				PartPose.offset(0.0f, 11.4673f, -6.6748f)
			)

			backpack.addOrReplaceChild(
				"middle_3_r1",
				CubeListBuilder.create().texOffs(38, 0)
					.addBox(-2.0f, -3.0009f, 0.0151f, 4.0f, 3.0f, 2.0f, CubeDeformation(0.0f)),
				PartPose.offsetAndRotation(0.0f, -8.4914f, -1.5153f, -0.9163f, 0.0f, 0.0f)
			)
			backpack.addOrReplaceChild(
				"middle_2_r1",
				CubeListBuilder.create().texOffs(44, 4)
					.addBox(-2.0f, -10.0009f, 0.0151f, 4.0f, 10.0f, 6.0f, CubeDeformation(0.0f)),
				PartPose.offsetAndRotation(0.0f, 0.7336f, -5.3403f, -0.3927f, 0.0f, 0.0f)
			)

			return meshDefinition
		}
	}
}
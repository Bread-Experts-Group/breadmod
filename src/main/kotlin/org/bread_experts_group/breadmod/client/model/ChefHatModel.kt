package org.bread_experts_group.breadmod.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.model.Model
import net.minecraft.client.model.geom.EntityModelSet
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.model.ChefHatModel.Companion.HAT_TEXTURE
import org.bread_experts_group.breadmod.client.render.entity.layers.ChefHatArmorLayer
import org.bread_experts_group.breadmod.registry.item.actual.armor.ChefHatItem
import org.bread_experts_group.breadmod.util.render.localClient

/**
 * Model data for the Chef Hat.
 *
 * @see ChefHatItem
 * @see ChefHatArmorLayer
 * @author Logan McLean
 * @since 1.0.0
 */
class ChefHatModel() : Model(RenderType::entityTranslucent) {
	constructor(modelSet : EntityModelSet) : this() {
		modelSet.bakeLayer(Companion.HAT_LAYER)
	}
	/**
	 * Bakes and renders this model to the buffer.
	 */
	override fun renderToBuffer(
		poseStack : PoseStack,
		buffer : VertexConsumer,
		packedLight : Int,
		packedOverlay : Int,
		color : Int
	) {
		localClient.entityModels.bakeLayer(Companion.HAT_LAYER)
			.render(poseStack, buffer, packedLight, packedOverlay, color)
	}
	/**
	 * [renderToBuffer] with the [VertexConsumer] already specified
	 */
	fun render(
		poseStack : PoseStack,
		packedLight : Int,
		packedOverlay : Int,
		color : Int
	) : Unit = this.renderToBuffer(
		poseStack,
		localClient.renderBuffers().bufferSource().getBuffer(this.renderType(Companion.HAT_TEXTURE)),
		packedLight,
		packedOverlay,
		color
	)

	companion object {
		/**
		 * Model layer location for the chef hat.
		 *
		 * @see HAT_TEXTURE
		 */
		val HAT_LAYER : ModelLayerLocation = ModelLayerLocation(modLocation("chef_hat"), "main")
		/**
		 * Texture location for the chef hat.
		 */
		val HAT_TEXTURE : ResourceLocation = modLocation("textures/models/armor/chef_hat.png")
		fun createLayerDefinition() : LayerDefinition =
			LayerDefinition.create(this.createMesh(), 40, 27)

		private fun createMesh() : MeshDefinition {
			val meshDefinition = MeshDefinition()
			val partDefinition = meshDefinition.root

			partDefinition.addOrReplaceChild(
				"chef_hat", CubeListBuilder.create().texOffs(0, 0)
					.texOffs(0, 0)
					.addBox(-4.5f, -3f, -4.5f, 9f, 3f, 9f)
					.texOffs(0, 12)
					.addBox(-5f, -8f, -5f, 10f, 5f, 10f),
				PartPose.ZERO
			)

			return meshDefinition
		}
	}
}
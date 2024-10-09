package bread.mod.breadmod.client.model

import bread.mod.breadmod.ModMainCommon.modLocation
import bread.mod.breadmod.item.armor.ChefHatItem
import bread.mod.breadmod.util.render.rgMinecraft
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
import bread.mod.breadmod.client.render.entity.layers.ChefHatArmorLayer

/**
 * Model data for the Chef Hat.
 *
 * @see ChefHatItem
 * @see ChefHatArmorLayer
 * @author Logan McLean
 * @since 1.0.0
 */
class ChefHatModel() : Model(RenderType::entityTranslucent) {
    constructor(modelSet: EntityModelSet) : this() {
        modelSet.bakeLayer(HAT_LAYER)
    }

    /**
     * Bakes and renders this model to the buffer.
     */
    override fun renderToBuffer(
        poseStack: PoseStack,
        buffer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        createLayerDefinition().bakeRoot().render(poseStack, buffer, packedLight, packedOverlay, color)
    }

    /**
     * [renderToBuffer] with the [VertexConsumer] already specified
     */
    fun render(
        poseStack: PoseStack,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) = renderToBuffer(
        poseStack,
        rgMinecraft.renderBuffers().bufferSource().getBuffer(renderType(HAT_TEXTURE)),
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
        val HAT_LAYER: ModelLayerLocation = ModelLayerLocation(modLocation("chef_hat"), "main")

        /**
         * Texture location for the chef hat.
         */
        val HAT_TEXTURE: ResourceLocation = modLocation("textures/models/armor/chef_hat.png")

        fun createLayerDefinition(): LayerDefinition =
            LayerDefinition.create(createMesh(), 40, 27)

        private fun createMesh(): MeshDefinition {
            val meshDefinition = MeshDefinition()
            val partDefinition = meshDefinition.root

            partDefinition.addOrReplaceChild(
                "chef_hat", CubeListBuilder.create().texOffs(0, 0)
                    .texOffs(0, 0)
                    .addBox(-4.5f, -3f, -4.5f, 9f, 3f, 9f,)
                    .texOffs(0, 12)
                    .addBox(-5f, -8f, -5f, 10f, 5f, 10f,),
                PartPose.ZERO
            )

            return meshDefinition
        }
    }
}
package bread.mod.breadmod.client.model

import bread.mod.breadmod.ModMainCommon.modLocation
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
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation
import java.awt.Color

class ChefHatModel() : Model(RenderType::entitySolid) {
    constructor(modelSet: EntityModelSet) : this() {
        modelSet.bakeLayer(HAT_LAYER)
    }

    companion object {
        val HAT_LAYER: ModelLayerLocation = ModelLayerLocation(modLocation("chef_hat"), "main")
        private val HAT_TEXTURE: ResourceLocation = modLocation("models/armor/chef_hat")

        fun createMesh(): MeshDefinition {
            val meshDefinition = MeshDefinition()
            val parDefinition = meshDefinition.root

            parDefinition.addOrReplaceChild(
                "hat", CubeListBuilder.create().texOffs(0, 0).addBox(
                    -4f, -8f, -4f, 8f, 8f, 8f,
                    CubeDeformation(1f)
                ), PartPose.ZERO
            )

            return meshDefinition
        }

        fun createLayerDefinition(): LayerDefinition =
            LayerDefinition.create(createMesh(), 32, 32)
    }

    fun render(
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        renderToBuffer(
            poseStack,
            buffer.getBuffer(renderType(HAT_TEXTURE)),
            packedLight,
            packedOverlay,
            Color.WHITE.rgb
        )
    }

    // ItemRenderer.getFoilBufferDirect(buffer, renderType(HAT_TEXTURE), false, false)

    override fun renderToBuffer(
        poseStack: PoseStack,
        buffer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        createLayerDefinition().bakeRoot().render(poseStack, buffer, packedLight, packedOverlay)
    }
}
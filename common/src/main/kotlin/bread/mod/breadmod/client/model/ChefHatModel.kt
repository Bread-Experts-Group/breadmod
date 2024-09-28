package bread.mod.breadmod.client.model

import bread.mod.breadmod.ModMainCommon.modLocation
import bread.mod.breadmod.item.armor.ChefHatItem
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.model.Model
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation

/**
 * Model data for the Chef Hat.
 *
 * @see ChefHatItem
 * @see ChefHatArmorLayer
 * @author Logan McLean
 * @since 1.0.0
 */
class ChefHatModel : Model(RenderType::entitySolid) {
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

    companion object {
        /**
         * Texture location for the chef hat.
         */
        val HAT_TEXTURE: ResourceLocation = modLocation("textures/models/armor/chef_hat.png")
    }

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

    private fun createLayerDefinition(): LayerDefinition =
        LayerDefinition.create(createMesh(), 40, 27)
}
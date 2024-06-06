package breadmod.item.rendering

import breadmod.ModMain.modLocation
import breadmod.item.rendering.helper.AbstractRenderedItemModelRenderer
import breadmod.item.rendering.helper.PartialItemModelRenderer
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraftforge.client.model.generators.ModelProvider

class CustomToolGunItemRenderer: AbstractRenderedItemModelRenderer() {
    private val coilModelLocation = modLocation("${ModelProvider.ITEM_FOLDER}/tool_gun_coil_model")

    override fun render(
        pStack: ItemStack,
        pModel: CustomRenderedItemModel,
        pRenderer: PartialItemModelRenderer,
        pDisplayContext: ItemDisplayContext,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int,
        pPackedOverlay: Int
    ) {
        val coilModel = Minecraft.getInstance().modelManager.getModel(coilModelLocation)
        println("it worked")

        pRenderer.render(pModel.getOriginalModel(), pPackedLight)

        pPoseStack.translate(0.5, 0.0, 0.0)
        pRenderer.render(coilModel, pPackedLight)
    }
}
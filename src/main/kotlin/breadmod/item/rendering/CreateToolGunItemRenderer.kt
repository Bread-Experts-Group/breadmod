package breadmod.item.rendering

import breadmod.ModMain

import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraftforge.client.model.generators.ModelProvider

class CreateToolGunItemRenderer: CustomRenderedItemModelRenderer() {
    private val coilModelLocation = ResourceLocation(ModMain.ID, "${ModelProvider.ITEM_FOLDER}/tool_gun_coil_model")

    override fun render(
        stack: ItemStack,
        model: CustomRenderedItemModel,
        renderer: PartialItemModelRenderer,
        transformType: ItemDisplayContext,
        ms: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int
    ) {
        val coilModel = Minecraft.getInstance().modelManager.getModel(coilModelLocation)

        renderer.render(model.originalModel, light)
        renderer.render(coilModel, light)
    }
}
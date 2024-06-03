package breadmod.item

import breadmod.ModMain
import com.jozufozu.flywheel.core.PartialModel
import com.jozufozu.flywheel.util.AnimationTickHolder
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

class ToolGunItemRenderer: CustomRenderedItemModelRenderer() {
    private val coil: PartialModel = PartialModel(ResourceLocation(ModMain.ID, "item/coil"))

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
        val worldTime = AnimationTickHolder.getRenderTime() / 20
        var angle = worldTime * -30
        angle %= 360

        renderer.render(model.originalModel, light)
        ms.mulPose(Axis.XN.rotationDegrees(angle))
        renderer.render(coil.get(), light)
    }
}
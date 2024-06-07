package breadmod.item.rendering.helper

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraftforge.client.extensions.common.IClientItemExtensions
import net.minecraftforge.client.model.data.ModelData
import kotlin.properties.Delegates

object PartialItemModelRenderer {
    private val random = RandomSource.create()

    private lateinit var stack: ItemStack
    private lateinit var displayContext: ItemDisplayContext
    private lateinit var poseStack: PoseStack
    private lateinit var buffer: MultiBufferSource
    private var overlay by Delegates.notNull<Int>()

    fun of(
        pStack: ItemStack,
        pDisplayContext: ItemDisplayContext,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pOverlay: Int
    ): PartialItemModelRenderer {
        val instance = PartialItemModelRenderer
        stack = pStack
        displayContext = pDisplayContext
        poseStack = pPoseStack
        buffer = pBuffer
        overlay = pOverlay
        return instance
    }

    fun render(pModel: BakedModel, pLight: Int) {
        render(pModel, RenderType.solid(), pLight)
    }

    private fun render(pModel: BakedModel, pType: RenderType, pLight: Int) {
        if(stack.isEmpty) return

        poseStack.pushPose()
        poseStack.translate(-0.5, -0.5, -0.5)

        if(!pModel.isCustomRenderer) {
            println("item is not a custom renderer!")
            val vertexConsumer = ItemRenderer.getFoilBufferDirect(buffer, pType, true, stack.hasFoil())
            for(pass: BakedModel in pModel.getRenderPasses(stack, false)) {
                renderBakedItemModel(pass, pLight, poseStack, vertexConsumer)
            }
        } else {
            println("item is a custom renderer!")
            IClientItemExtensions.of(stack)
                .customRenderer
                .renderByItem(stack, displayContext, poseStack, buffer, pLight, overlay)
        }

        poseStack.popPose()
    }

    fun renderBakedItemModel(pModel: BakedModel, pLight: Int, pPoseStack: PoseStack, pVertexConsumer: VertexConsumer) {
        val itemRenderer: ItemRenderer = Minecraft.getInstance().itemRenderer
        val data: ModelData = ModelData.EMPTY

        for(renderType: RenderType in pModel.getRenderTypes(stack, false)) {
            for(direction: Direction in Direction.entries.toTypedArray()) {
                random.setSeed(42L)
                itemRenderer.renderQuadList(pPoseStack, pVertexConsumer, pModel.getQuads(null, direction, random, data, renderType), stack, pLight, overlay)
            }

            random.setSeed(42L)
            itemRenderer.renderQuadList(pPoseStack, pVertexConsumer, pModel.getQuads(null, null, random, data, renderType), stack, pLight, overlay)
        }
    }
}
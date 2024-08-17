package breadmod.client.gui

import breadmod.util.render.minecraft
import breadmod.util.render.scaleFlat
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.block.BlockRenderDispatcher
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher
import net.minecraft.client.renderer.entity.EntityRenderDispatcher
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.network.chat.Component
import net.minecraftforge.client.gui.overlay.ForgeGui
import net.minecraftforge.client.gui.overlay.IGuiOverlay

@Suppress("MemberVisibilityCanBePrivate", "Unused")
abstract class AbstractModGuiOverlay: IGuiOverlay {
    val entityRenderer: EntityRenderDispatcher = minecraft.entityRenderDispatcher
    val itemRenderer: ItemRenderer = minecraft.itemRenderer
    val blockRenderer: BlockRenderDispatcher = minecraft.blockRenderer
    val blockEntityRenderer: BlockEntityRenderDispatcher = minecraft.blockEntityRenderDispatcher

    final override fun render(
        gui: ForgeGui,
        guiGraphics: GuiGraphics,
        partialTick: Float,
        screenWidth: Int,
        screenHeight: Int
    ) {
        val player = gui.minecraft.player ?: return
        val poseStack = guiGraphics.pose()
        val bufferSource = guiGraphics.bufferSource()

        renderOverlay(gui, guiGraphics, partialTick, screenWidth, screenHeight, poseStack, bufferSource, player)
    }

    abstract fun renderOverlay(
        pGui: ForgeGui,
        pGuiGraphics: GuiGraphics,
        pPartialTick: Float,
        pScreenWidth: Int,
        pScreenHeight: Int,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPlayer: LocalPlayer
    )

    fun drawScaledText(
        pText: Component,
        pPose: PoseStack,
        pGuiGraphics: GuiGraphics,
        pGui: ForgeGui,
        pX: Int,
        pY: Int,
        pColor: Int,
        pScale: Float,
        pDropShadow: Boolean
    ) {
        pPose.scaleFlat(pScale)
        pGuiGraphics.drawString(
            pGui.minecraft.font,
            pText,
            pX,
            pY,
            pColor,
            pDropShadow
        )
        pPose.scaleFlat(1f)
    }
}
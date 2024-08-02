package breadmod.client.hud

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
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
    val instance: Minecraft = Minecraft.getInstance()
    val entityRenderer: EntityRenderDispatcher = instance.entityRenderDispatcher
    val itemRenderer: ItemRenderer = instance.itemRenderer
    val blockRenderer: BlockRenderDispatcher = instance.blockRenderer
    val blockEntityRenderer: BlockEntityRenderDispatcher = instance.blockEntityRenderDispatcher

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

    fun PoseStack.scaleFlat(scale: Float) = this.scale(scale, scale, scale)

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
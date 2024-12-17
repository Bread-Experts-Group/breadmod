package org.bread_experts_group.breadmod.compat.lookingat.jade

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.Util
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderType
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.phys.Vec2
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.util.formatNumber
import org.bread_experts_group.breadmod.util.render.localClient
import snownee.jade.api.config.IWailaConfig.IConfigOverlay
import snownee.jade.api.fluid.JadeFluidObject
import snownee.jade.api.ui.Element
import snownee.jade.overlay.DisplayHelper
import snownee.jade.overlay.OverlayRenderer
import java.awt.Color
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

class FluidBarElement(
    private val fluid: JadeFluidObject,
    private val capacity: Int,
    private val direction: Direction?
) : Element() {
    companion object {
        private val defaultSize = Vec2(150f, 14f)
    }

    override fun getSize(): Vec2 = size ?: defaultSize

    private val uvs = mapOf(
        *listOf(
            null,
            Direction.DOWN,
            Direction.UP,
            Direction.NORTH,
            Direction.WEST,
            Direction.SOUTH,
            Direction.EAST,
        ).mapIndexed { index, direction -> direction to index * 16 }.toTypedArray()
    )

    private fun fill(guiGraphics: GuiGraphics, minX: Float, minY: Float, maxX: Float, maxY: Float) {
        var minX = minX
        var minY = minY
        var maxX = maxX
        var maxY = maxY
        val matrix = guiGraphics.pose().last().pose()
        var j: Float
        if (minX < maxX) {
            j = minX
            minX = maxX
            maxX = j
        }

        if (minY < maxY) {
            j = minY
            minY = maxY
            maxY = j
        }

        val color = IConfigOverlay.applyAlpha(Color.WHITE.rgb, OverlayRenderer.alpha)
        val sprite = localClient.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(modLocation("block", "bread_block"))
        val buffer = guiGraphics.bufferSource().getBuffer(RenderType.solid())

        fun addVertex(x: Float, y: Float, u: Float, v: Float) = buffer
            .addVertex(matrix, x, y, 0f)
            .setUv(u, v)
            .setNormal(0f, 0f, 0f)
            .setLight(0x0F000F0)
            .setColor(color)
        addVertex(minX, maxY, sprite.u0, sprite.v1)
        addVertex(maxX, maxY, sprite.u1, sprite.v1)
        addVertex(maxX, minY, sprite.u1, sprite.v0)
        addVertex(minX, minY, sprite.u0, sprite.v0)
        guiGraphics.flush()
    }

    private fun drawBorder(
        guiGraphics: GuiGraphics,
        minX: Float,
        minY: Float,
        maxX: Float,
        maxY: Float
    ) {
        fill(guiGraphics, minX + 1, minY, maxX - 1, minY + 1)
        fill(guiGraphics, minX + 1, maxY - 1, maxX - 1, maxY)
        fill(guiGraphics, minX, minY + 1, minX + 1, maxY - 1)
        fill(guiGraphics, maxX - 1, minY + 1, maxX, maxY - 1)
    }

    private fun GuiGraphics.renderScrollingStringBM(
        font: Font, text: Component, minX: Int, minY: Int, maxX: Int, maxY: Int, color: Int
    ) = renderScrollingStringBM(font, text, (minX + maxX) / 2, minX, minY, maxX, maxY, color)

    private fun GuiGraphics.renderScrollingStringBM(
        font: Font,
        text: Component,
        centerX: Int,
        minX: Int,
        minY: Int,
        maxX: Int,
        maxY: Int,
        color: Int
    ) {
        val i = font.width(text)
        val j = (minY + maxY - 9) / 2 + 1
        val k = maxX - minX
        if (i > k) {
            val l = i - k
            val d0 = Util.getMillis().toDouble() / 1000.0
            val d1 = max(l.toDouble() * 0.5, 3.0)
            val d2 = sin((Math.PI / 2) * cos((Math.PI * 2) * d0 / d1)) / 2.0 + 0.5
            val d3 = Mth.lerp(d2, 0.0, l.toDouble())
            fill(minX, minY, maxX, maxY, color)
//            this.enableScissor(minX, minY, maxX, maxY)
            this.drawString(font, text, minX - d3.toInt(), j, color)
//            this.disableScissor()
        } else {
            val i1 = Mth.clamp(centerX, minX + i / 2, maxX - i / 2)
            this.drawCenteredString(font, text, i1, j, color)
        }
    }

    private fun GuiGraphics.drawScrollingStringBM(font: Font, text: Component, minX: Int, maxX: Int, y: Int, color: Int): Int {
        val maxWidth = maxX - minX
        val textWidth = font.width(text.visualOrderText)
        if (textWidth <= maxWidth) {
            return this.drawString(font, text, minX, y, color)
        } else {
            this.renderScrollingStringBM(font, text, minX, y, maxX, y + font.lineHeight, color)
            return maxWidth
        }
    }

    override fun render(guiGraphics: GuiGraphics, x: Float, y: Float, maxX: Float, maxY: Float) {
        val poseStack = guiGraphics.pose()
        poseStack.pushPose()

        // Fluid Box
        drawBorder(
            guiGraphics,
            x, y,
            x + 80, y + 14
        )
        DisplayHelper.INSTANCE.drawFluid(
            guiGraphics,
            x + 1,
            y + 1,
            fluid,
            ((fluid.amount.toFloat() / capacity) * 78),
            12f,
            JadeFluidObject.bucketVolume()
        )

        // Fluid Name
        guiGraphics.drawScrollingStringBM(
            localClient.font,
            Component.translatable(if (!fluid.isEmpty) fluid.type.fluidType.descriptionId else "tooltip.jade.empty"),
            (x + 2).toInt(),
            (x + 78).toInt(),
            y.toInt() + 3,
            Color.WHITE.rgb
        )

        // Direction Sprite
        RenderSystem.enableBlend()
        guiGraphics.blit(
            modLocation("textures", "gui", "cube_sprites.png"),
            x.toInt() + 82, y.toInt() - 1,
            uvs[direction] ?: 0, 0,
            16, 16
        )
        RenderSystem.disableBlend()

        // Fluid Amount
        val (truncatedAmount, unit) = formatNumber(fluid.amount.toDouble(), -1)
        val asString = String.format("%07.2f", truncatedAmount)
        var zeros = ""
        for (char in asString) {
            if (char != '0' && char != '.') break
            zeros += char
        }
        val zeroPadSize = localClient.font.width(zeros)
        guiGraphics.drawString(
            localClient.font,
            zeros,
            x + 100,
            y + 3,
            Color.GRAY.rgb,
            true
        )
        val filledString = "${asString.substring(zeros.length)} ${unit}B"
        guiGraphics.drawString(
            localClient.font,
            filledString,
            x + 100 + zeroPadSize,
            y + 3,
            Color.WHITE.rgb,
            true
        )
        size = defaultSize.add(Vec2((zeroPadSize + localClient.font.width(filledString)).toFloat(), 0f))
        poseStack.popPose()
    }
}
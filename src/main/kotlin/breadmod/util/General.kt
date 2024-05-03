package breadmod.util

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.level.material.Fluid
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions
import org.joml.Matrix4f
import java.awt.Color
import kotlin.math.abs
import kotlin.math.min

val formatArray = listOf("p", "n", "m", "", "k", "M", "G", "T", "P", "E")
fun formatNumber(number: Double, offset: Int): Pair<Double, String> {
    var num = number
    var index = 3 + offset
    while (num >= 1000 && index < formatArray.size - 1) {
        num /= 1000
        index++
    }
    while (num < 1 && index > 0) {
        num *= 1000
        index--
    }
    return num to formatArray[index]
}

fun formatUnit(from: Double, to: Double, unit: String, formatShort: Boolean, places: Int, offset: Int = 0): String {
    val formatStr = "%.${places}f %s/ %.${places}f %s (%.${places}f%%)"
    val percent = (from / to) * 100
    if (formatShort) {
        val toFormat = formatNumber(to, offset)
        val fromFormat = formatNumber(from, offset)
        return String.format(
            formatStr,
            fromFormat.first, if(toFormat.second != fromFormat.second) "${fromFormat.second}$unit " else "",
            toFormat.first, toFormat.second + unit,
            percent
        )
    } else {
        return String.format(
            formatStr,
            from, "",
            to, unit,
            percent
        )
    }
}
fun formatUnit(from: Int, to: Int, unit: String, formatShort: Boolean, places: Int, offset: Int = 0): String =
    formatUnit(from.toDouble(), to.toDouble(), unit, formatShort, places, offset)

fun GuiGraphics.renderFluid(pX: Float, pY: Float, pWidth: Int, pHeight: Int, fluid: Fluid, flowing: Boolean) {
    val minecraft = Minecraft.getInstance()
    val nWidth = abs(pWidth)

    val ext = IClientFluidTypeExtensions.of(fluid)
    val texture = if(flowing) ext.flowingTexture else ext.stillTexture
    val sprite = minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture)
    val colors = FloatArray(4).also { Color(ext.tintColor).getComponents(it) }

    val spriteData = sprite.contents()
    val matrix4f: Matrix4f = this.pose().last().pose()

    RenderSystem.setShaderTexture(0, sprite.atlasLocation())
    RenderSystem.setShader { GameRenderer.getPositionColorTexShader() }
    RenderSystem.enableBlend()
    val bufferBuilder = Tesselator.getInstance().builder
    bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX)

    val pX2 = pX + nWidth

    var v1: Float = sprite.v1; var u1: Float = sprite.u1
    var remainingFluid = pHeight; var ranDiff = false
    while(remainingFluid > 0) {
        val lpY = (pY - remainingFluid); val lpY2 = lpY + min(remainingFluid, nWidth)

        if(flowing && !ranDiff) {
            val stillSprite = minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ext.stillTexture)
            val diff = (stillSprite.contents().width().toFloat() / spriteData.width())
            v1 = sprite.v0 + ((v1 - sprite.v0) * diff)
            u1 = sprite.u0 + ((u1 - sprite.u0) * diff)

            ranDiff = true
        }
        if(remainingFluid < nWidth) v1 = sprite.v0 + (((v1 - sprite.v0) / nWidth) * remainingFluid)

        fun VertexConsumer.color() = this.color(colors[0], colors[1], colors[2], colors[3])
        val flipped = pWidth < 0
        val fX1 = if(flipped) pX else pX2; val fX2 = if(flipped) pX2 else pX
        val fY1 = if(flipped) lpY else lpY2; val fY2 = if(flipped) lpY2 else lpY
        bufferBuilder.vertex(matrix4f, fX2, fY2, 0F).color().uv(sprite.u0, sprite.v0).endVertex()
        bufferBuilder.vertex(matrix4f, fX2, fY1, 0F).color().uv(sprite.u0, v1).endVertex()
        bufferBuilder.vertex(matrix4f, fX1, fY1, 0F).color().uv(u1, v1).endVertex()
        bufferBuilder.vertex(matrix4f, fX1, fY2, 0F).color().uv(u1, sprite.v0).endVertex()

        remainingFluid -= nWidth
    }

    BufferUploader.drawWithShader(bufferBuilder.end())
    RenderSystem.disableBlend()
}
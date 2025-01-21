package org.bread_experts_group.breadmod.compat.lookingat.jade

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.ChatFormatting
import net.minecraft.Util
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderType
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.util.Mth
import net.minecraft.world.inventory.InventoryMenu
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.util.formatNumberBigDecimal
import org.joml.Math.clamp
import snownee.jade.api.config.IWailaConfig.IConfigOverlay
import snownee.jade.overlay.OverlayRenderer
import java.awt.Color
import java.math.BigDecimal
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

object JadeDrawingCommon {
	val uvs: Map<Direction?, Int> = mapOf(
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

	fun GuiGraphics.drawDirectionCube(x: Float, y: Float, direction: Direction?) {
		RenderSystem.enableBlend()
		this.blit(
			modLocation("textures", "gui", "cube_sprites.png"),
			x.toInt() + 82, y.toInt() - 1,
			this@JadeDrawingCommon.uvs[direction] ?: 0, 0,
			16, 16
		)
		RenderSystem.disableBlend()
	}

	fun GuiGraphics.fill(minX: Float, minY: Float, maxX: Float, maxY: Float) {
		var minX = minX
		var minY = minY
		var maxX = maxX
		var maxY = maxY
		val matrix = this.pose().last().pose()
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
		val buffer = this.bufferSource().getBuffer(RenderType.solid())
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
		this.flush()
	}

	fun GuiGraphics.drawBorder(
		minX: Float,
		minY: Float,
		maxX: Float,
		maxY: Float
	) {
		this.fill(minX + 1, minY, maxX - 1, minY + 1)
		this.fill(minX + 1, maxY - 1, maxX - 1, maxY)
		this.fill(minX, minY + 1, minX + 1, maxY - 1)
		this.fill(maxX - 1, minY + 1, maxX, maxY - 1)
	}

	fun GuiGraphics.renderScrollingStringBM(
		font: Font, text: Component, minX: Int, minY: Int, maxX: Int, maxY: Int, color: Int
	): Unit = this.renderScrollingStringBM(font, text, (minX + maxX) / 2, minX, minY, maxX, maxY, color)

	fun GuiGraphics.renderScrollingStringBM(
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
			val matrix = this.pose().last().pose()
			val left = matrix.m30().toInt()
			val top = matrix.m31().toInt()
			this.enableScissor(left + minX, top + minY, left + maxX, top + maxY)
			this.drawString(font, text, minX - d3.toInt(), j, color)
			this.disableScissor()
		} else {
			val i1 = clamp(centerX, minX + i / 2, maxX - i / 2)
			this.drawCenteredString(font, text, i1, j, color)
		}
	}

	fun GuiGraphics.drawScrollingStringBM(
		font: Font,
		text: Component,
		minX: Int,
		maxX: Int,
		y: Int,
		color: Int
	): Int {
		val maxWidth = maxX - minX
		val textWidth = font.width(text.visualOrderText)
		if (textWidth <= maxWidth) {
			return this.drawString(font, text, minX, y, color)
		}
		this.renderScrollingStringBM(font, text, minX, y, maxX, y + font.lineHeight, color)
		return maxWidth
	}

	fun fixedLengthNumberedComponent(
		n: BigDecimal?,
		offset: Int = 0,
		gray: Style,
		darkGray: Style
	): Pair<MutableComponent, String> {
		if (n != null) {
			val (truncatedAmount, unit) = formatNumberBigDecimal(n, offset)
			val asString = String.format("%07.2f", truncatedAmount)
			var zeros = ""
			for (char in asString) {
				if (char != '0' && char != '.') break
				zeros += char
			}
			return Component
				.literal(zeros).withStyle(darkGray)
				.append(Component.literal(asString.substring(zeros.length)).withStyle(gray)) to unit
		}
		return Component.literal("∞").withStyle(ChatFormatting.GOLD) to ""
	}

	private val darkGrayArray = Color(ChatFormatting.DARK_GRAY.color!!).getComponents(null)
	private val grayArray = Color(ChatFormatting.GRAY.color!!).getComponents(null)

	fun fixedLengthScrollingComponent(
		n: BigDecimal,
		cap: BigDecimal?,
		unitName: String,
		offset: Int = 0,
		tint: Int = Color.WHITE.rgb
	): MutableComponent {
		val tintArray = Color(tint).getComponents(null)
		val tintedDarkGray = Style.EMPTY.withColor(
			Color(
				(tintArray[0] * this.darkGrayArray[0]).toFloat(),
				(tintArray[1] * this.darkGrayArray[1]).toFloat(),
				(tintArray[2] * this.darkGrayArray[2]).toFloat()
			).rgb
		)
		val tintedGray = Style.EMPTY.withColor(
			Color(
				(tintArray[0] * this.grayArray[0]).toFloat(),
				(tintArray[1] * this.grayArray[1]).toFloat(),
				(tintArray[2] * this.grayArray[2]).toFloat()
			).rgb
		)

		val (truncated, unit) = this.fixedLengthNumberedComponent(
			n,
			offset,
			tintedGray,
			tintedDarkGray
		)
		val (truncatedCapacity, unitCapacity) = this.fixedLengthNumberedComponent(
			cap,
			offset,
			tintedGray,
			tintedDarkGray
		)

		return truncated
			.append(
				if (unit == unitCapacity) Component.empty()
				else Component.literal(" ${unit}$unitName").withStyle(tintedGray)
			)
			.append(Component.literal(" | ").withStyle(tintedDarkGray))
			.append(truncatedCapacity)
			.append(Component.literal(" ${unitCapacity}$unitName").withStyle(tintedGray))
	}
}
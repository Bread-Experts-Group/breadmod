package org.bread_experts_group.breadmod.client.render.texture

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.metadata.gui.GuiMetadataSection
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling.Type.NINE_SLICE
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth.clamp
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.localClient

/**
 * Contains the [location], [textureWidth], and [textureHeight] of a texture.
 * Blit functions are also provided for easy rendering of textures.
 *
 * [textureWidth] and [textureHeight] default to 16.
 */
// todo convert to be a widget that can be added to screens (side todo, make a screen impl that utilises this GuiElement)
class GuiElement(
	val location: ResourceLocation,
	val textureWidth: Int = 16,
	val textureHeight: Int = 16
) : AbstractWidget() {
	companion object {
		val MISSING: GuiElement = GuiElement(MissingTextureAtlasSprite.getLocation())
		val BLOCKHEAD: GuiElement = GuiElement(
			modLocation("textures", "tool_gun", "gui", "blockhead.png"),
			256, 256
		)
	}

	private val isSprite: Boolean
	private val spriteScalingType: GuiSpriteScaling.Type
	private val atlasSprite: TextureAtlasSprite
	private val color: IntArray = intArrayOf(255, 255, 255, 255)

	override fun getTooltip(): Tooltip? {
		return super.getTooltip()
	}

	// todo implement the blit method into this render method
	//  figure out a solution to determine if the sprite needs to be drawn as static, nine-sliced scaling, or normal
	override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		TODO("Not yet implemented")
	}

	override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {
		TODO("Not yet implemented")
	}

	private fun getAsStaticTexture() =
		this.atlasSprite.contents().name().withPrefix("textures/gui/sprites/").withSuffix(".png")

	init {
		val sprite = localClient.guiSprites.getSprite(this.location)
		if (sprite.contents().name() == MissingTextureAtlasSprite.getLocation()) {
			this.isSprite = false
			this.spriteScalingType = GuiSpriteScaling.DEFAULT.type()
			this.atlasSprite = localClient.guiSprites.getSprite(MissingTextureAtlasSprite.getLocation())
		} else {
			this.isSprite = true
			this.spriteScalingType = sprite.contents().metadata().getSection(GuiMetadataSection.TYPE)
				.orElse(GuiMetadataSection.DEFAULT).scaling.type()
			this.atlasSprite = sprite
		}
	}

	fun setColor(red: Int, green: Int, blue: Int, alpha: Int = 255): GuiElement = this.also { element ->
		element.color[0] = red
		element.color[1] = green
		element.color[2] = blue
		element.color[3] = alpha
	}

	private fun resetColor() {
		this.color[0] = 255
		this.color[1] = 255
		this.color[2] = 255
		this.color[3] = 255
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
	}

	/**
	 * Convenience Function for blitting textures.
	 * @param x the x-coordinate of the top-left corner of the blit position.
	 * @param y the y-coordinate of the top-left corner of the blit position.
	 * @param uOffset the horizontal texture coordinate offset.
	 * @param vOffset the vertical texture coordinate offset.
	 * @param uWidth the width of the blitted portion.
	 * @param vHeight the height of the blitted portion.
	 * @param textureWidth  the width of the texture.
	 * @param textureHeight the height of the texture.
	 * */
	fun blit(
		guiGraphics: GuiGraphics,
		x: Int,
		y: Int,
		uOffset: Float = 0f,
		vOffset: Float = 0f,
		uWidth: Int = this.textureWidth,
		vHeight: Int = this.textureHeight,
		textureWidth: Int = this.textureWidth,
		textureHeight: Int = this.textureHeight
	) {
		RenderSystem.setShaderColor(
			(this.color[0] / 255f),
			(this.color[1] / 255f),
			(this.color[2] / 255f),
			(this.color[3] / 255f)
		)
		if (this.isSprite) {
			if (this.spriteScalingType == NINE_SLICE) {
				guiGraphics.blitSprite(this.location, x, y, textureWidth, textureHeight)
			} else guiGraphics.blitSprite(
				this.location,
				textureWidth,
				textureHeight,
				uOffset.toInt(),
				vOffset.toInt(),
				x,
				y,
				uWidth,
				vHeight
			)
		} else guiGraphics.blit(this.location, x, y, uOffset, vOffset, uWidth, vHeight, textureWidth, textureHeight)
		this.resetColor()
	}

	fun blitScaled(guiGraphics: GuiGraphics, x: Int, y: Int, width: Int, height: Int): Unit =
		this.blit(
			guiGraphics,
			x,
			y,
			uWidth = width,
			vHeight = height,
			textureWidth = width,
			textureHeight = height
		)

	fun blitStaticSprite(guiGraphics: GuiGraphics, x: Int, y: Int, frame: Int = 0) {
		if (!this.isSprite) return
		val location = this.getAsStaticTexture()
		RenderSystem.setShaderColor(
			(this.color[0] / 255f),
			(this.color[1] / 255f),
			(this.color[2] / 255f),
			(this.color[3] / 255f)
		)
		val contents = this.atlasSprite.contents()
		guiGraphics.blit(
			location,
			x,
			y,
			0f,
			0f + (contents.height() * frame),
			this.textureWidth,
			this.textureHeight,
			contents.width(),
			contents.height() * (contents.uniqueFrames.max().asInt + 1)
		)
		this.resetColor()
	}

	/**
	 * Draws a progressive texture/sprite which defaults drawing from bottom to top.
	 * @param drawFromTop draws the texture from the top instead of the bottom.
	 */
	fun drawProgressiveVertical(
		guiGraphics: GuiGraphics,
		progressInput: Int,
		x: Int,
		y: Int,
		drawFromTop: Boolean
	): Unit = this.blit(
		guiGraphics,
		x,
		y + if (!drawFromTop) this.textureHeight - progressInput else 0,
		vOffset = 0f - if (!drawFromTop) progressInput else 0,
		vHeight = 0 + progressInput
	)

	// todo flipped logic
	fun drawProgressiveHorizontal(
		guiGraphics: GuiGraphics,
		progressInput: Int,
		x: Int,
		y: Int,
		drawFromRight: Boolean = false
	): Unit = this.blit(
		guiGraphics,
		x,
		y,
		uWidth = clamp(progressInput, 0, this.textureWidth)
	)
}
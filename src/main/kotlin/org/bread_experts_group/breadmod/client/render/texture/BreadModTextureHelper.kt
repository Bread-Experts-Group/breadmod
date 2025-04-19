package org.bread_experts_group.breadmod.client.render.texture

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth.clamp
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation

/**
 * Contains the [location], [textureWidth], and [textureHeight] of a texture.
 * Blit functions are also provided for easy rendering of textures.
 *
 * [textureWidth] and [textureHeight] default to 16.
 */
class BreadModTextureHelper(
	val location: ResourceLocation,
	val textureWidth: Int = 16,
	val textureHeight: Int = 16
) {
	companion object {
		val MISSING_TEXTURE: BreadModTextureHelper = BreadModTextureHelper(MissingTextureAtlasSprite.getLocation())
		val BLOCKHEAD_TEXTURE: BreadModTextureHelper = BreadModTextureHelper(
			modLocation("textures", "tool_gun", "gui", "blockhead.png"),
			256, 256
		)
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
	fun blitTexture(
		guiGraphics: GuiGraphics,
		x: Int,
		y: Int,
		uOffset: Float = 0f,
		vOffset: Float = 0f,
		uWidth: Int = this.textureWidth,
		vHeight: Int = this.textureHeight,
		textureWidth: Int = this.textureWidth,
		textureHeight: Int = this.textureHeight
	): Unit =
		guiGraphics.blit(this.location, x, y, uOffset, vOffset, uWidth, vHeight, textureWidth, textureHeight)

	/**
	 * Draws a progressive texture/sprite which defaults drawing from bottom to top.
	 * @param drawFromTop draws the texture from the top instead of the bottom.
	 */
	fun drawProgressiveSpriteVertical(
		guiGraphics: GuiGraphics,
		progressInput: Int,
		x: Int,
		y: Int,
		drawFromTop: Boolean
	): Unit = this.blitTexture(
		guiGraphics,
		x,
		y + if (!drawFromTop) this.textureHeight - progressInput else 0,
		vOffset = 0f - if (!drawFromTop) progressInput else 0,
		vHeight = 0 + progressInput
	)

	fun drawProgressiveSpriteHorizontal(guiGraphics: GuiGraphics, progressInput: Int, x: Int, y: Int): Unit =
		this.blitTexture(
			guiGraphics,
			x,
			y,
			uWidth = clamp(progressInput, 0, this.textureWidth)
		)
}
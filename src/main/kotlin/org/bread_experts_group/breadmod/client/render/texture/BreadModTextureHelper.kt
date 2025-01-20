package org.bread_experts_group.breadmod.client.render.texture

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite
import net.minecraft.resources.ResourceLocation

/**
 * Contains the [location], [textureWidth], and [textureHeight] of a texture.
 * Blit functions are also provided for easy rendering of textures.
 */
class BreadModTextureHelper(
	val location: ResourceLocation,
	val textureWidth: Int,
	val textureHeight: Int
) {
	companion object {
		val MISSING_TEXTURE: BreadModTextureHelper = BreadModTextureHelper(
			MissingTextureAtlasSprite.getLocation(),
			16, 16
		)
	}

	/**
	 * Convenience Function for blitting textures.
	 * @param x the x-coordinate of the top-left corner of the blit position.
	 * @param y the y-coordinate of the top-left corner of the blit position.
	 * @param uOffset the horizontal texture coordinate offset.
	 * @param vOffset the vertical texture coordinate offset.
	 * @param width the width of the blitted portion.
	 * @param height the height of the blitted portion.
	 * */
	fun blitTexture(
		guiGraphics: GuiGraphics,
		x: Int,
		y: Int,
		uOffset: Float = 0f,
		vOffset: Float = 0f,
		width: Int = this.textureWidth,
		height: Int = this.textureHeight
	): Unit =
		guiGraphics.blit(this.location, x, y, uOffset, vOffset, width, height, this.textureWidth, this.textureHeight)

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
		height = 0 + progressInput
	)
//	// todo work on mirrored logic
//	fun drawProgressiveSpriteHorizontal(guiGraphics: GuiGraphics, energyStored: Int, x: Int, y: Int): Unit =
//		this.blitTexture(
//			guiGraphics,
//			x,
//			y,
//			width = clamp(energyStored, 0, this.textureWidth)
//		)
}
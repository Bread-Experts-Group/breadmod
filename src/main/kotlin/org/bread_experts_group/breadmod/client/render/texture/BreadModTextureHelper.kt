package org.bread_experts_group.breadmod.client.render.texture

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth

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
		val CODEC: Codec<BreadModTextureHelper> = RecordCodecBuilder.create { inst ->
			inst.group(
				ResourceLocation.CODEC.fieldOf("location").forGetter(BreadModTextureHelper::location),
				Codec.INT.fieldOf("width").forGetter(BreadModTextureHelper::textureWidth),
				Codec.INT.fieldOf("height").forGetter(BreadModTextureHelper::textureHeight)
			).apply(inst, ::BreadModTextureHelper)
		}
		val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, BreadModTextureHelper> = StreamCodec.composite(
			ResourceLocation.STREAM_CODEC, BreadModTextureHelper::location,
			ByteBufCodecs.INT, BreadModTextureHelper::textureWidth,
			ByteBufCodecs.INT, BreadModTextureHelper::textureHeight,
			::BreadModTextureHelper
		)
		val MISSING_TEXTURE: BreadModTextureHelper = BreadModTextureHelper(
			ResourceLocation.withDefaultNamespace("missingno"),
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
	 * @param startFromTop draws the texture from the top instead of the bottom.
	 */
	fun drawProgressiveSpriteVertical(
		guiGraphics: GuiGraphics,
		progressInput: Int,
		x: Int,
		y: Int,
		startFromTop: Boolean
	): Unit = this.blitTexture(
		guiGraphics,
		x,
		y + if (!startFromTop) this.textureHeight - progressInput else 0,
		vOffset = 0f - if (!startFromTop) progressInput else 0,
		height = 0 + progressInput
	)

	fun drawProgressiveSpriteHorizontal(guiGraphics: GuiGraphics, energyStored: Int, x: Int, y: Int): Unit =
		this.blitTexture(
			guiGraphics,
			x,
			y,
			width = Mth.clamp(energyStored, 0, this.textureWidth)
		)
}
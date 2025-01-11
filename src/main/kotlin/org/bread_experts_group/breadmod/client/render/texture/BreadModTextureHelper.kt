package org.bread_experts_group.breadmod.client.render.texture

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation

/**
 * Contains the [location], [width], and [height] of a texture.
 * Blit functions are also provided for easy rendering of textures.
 */
class BreadModTextureHelper(
	val location: ResourceLocation,
	val width: Int,
	val height: Int
) {
	companion object {
		val CODEC: Codec<BreadModTextureHelper> = RecordCodecBuilder.create { inst ->
			inst.group(
				ResourceLocation.CODEC.fieldOf("location").forGetter(BreadModTextureHelper::location),
				Codec.INT.fieldOf("width").forGetter(BreadModTextureHelper::width),
				Codec.INT.fieldOf("height").forGetter(BreadModTextureHelper::height)
			).apply(inst, ::BreadModTextureHelper)
		}
		val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, BreadModTextureHelper> = StreamCodec.composite(
			ResourceLocation.STREAM_CODEC, BreadModTextureHelper::location,
			ByteBufCodecs.INT, BreadModTextureHelper::width,
			ByteBufCodecs.INT, BreadModTextureHelper::height,
			::BreadModTextureHelper
		)
		val MISSING_TEXTURE: BreadModTextureHelper = BreadModTextureHelper(
			MissingTextureAtlasSprite.getLocation(),
			16, 16
		)
	}

	fun blitTexture(guiGraphics: GuiGraphics, x: Int, y: Int, width: Int, height: Int): Unit =
		guiGraphics.blit(this.location, x, y, 0f, 0f, width, height, this.width, this.height)

	fun blitTexture(guiGraphics: GuiGraphics, x: Int, y: Int): Unit =
		this.blitTexture(guiGraphics, x, y, this.width, this.height)
}
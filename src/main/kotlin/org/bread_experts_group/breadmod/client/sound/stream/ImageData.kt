package org.bread_experts_group.breadmod.client.sound.stream

import com.google.common.hash.HashCode
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.resources.ResourceLocation
import org.bread_experts_group.breadmod.client.render.localClient

class ImageData(
	val width: Int,
	val height: Int,
	private val textureData: DynamicTexture?,
	val location: ResourceLocation
) {
	companion object {
		val EMPTY: ImageData = ImageData(
			16,
			16,
			null,
			ResourceLocation.withDefaultNamespace("textures/item/barrier.png")
		)
	}

	fun registerTexture() {
		check(this.textureData != null) { "Texture data cannot be null!" }
		localClient.textureManager.register(this.location, this.textureData)
	}

	override fun equals(other: Any?): Boolean = if (other is ImageData)
		other.width == this.width && other.height == this.height &&
				other.location == this.location && other.textureData == this.textureData else false

	override fun hashCode(): Int = this.location.hashCode()
}
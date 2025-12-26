package org.bread_experts_group.breadmod.client.render.texture

import com.mojang.blaze3d.platform.NativeImage
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.math.Axis
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.metadata.gui.GuiMetadataSection
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling.Type.NINE_SLICE
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth.clamp
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.solidColorTexture
import org.bread_experts_group.breadmod.client.render.translate
import java.util.Objects

// todo maybe look into making this an AbstractWidget in the future
//  (i need some method or toggle to tell the render part to render normally, scaled, static sprite frame, etc..)
//  (need to also figure out how to handle the jei recipe categories as they also use GuiElement)
/**
 * Contains the [location], [textureWidth], and [textureHeight] of a texture.
 * Blit functions are also provided for easy rendering of textures.
 *
 * [textureWidth] and [textureHeight] default to 16.
 */
class GuiElement(
	val location: ResourceLocation,
	val textureWidth: Int = 16,
	val textureHeight: Int = 16
) {
	companion object {
		val MISSING: GuiElement = GuiElement(MissingTextureAtlasSprite.getLocation())
		val BLOCKHEAD: GuiElement = GuiElement(
			modLocation("textures", "tool_gun", "gui", "blockhead.png"),
			256, 256
		)

		fun ofScaledCopy(element: GuiElement, width: Int, height: Int): GuiElement =
			GuiElement(element.location, width, height)

		fun ofSolidColor(r: Int, g: Int, b: Int): GuiElement = GuiElement(solidColorTexture(r, g, b, "gui_element"))
		fun ofSolidColor(color: Int): GuiElement = GuiElement(solidColorTexture(color, "gui_element"))
	}

	val isSprite: Boolean
	val isAnimatedSprite: Boolean
	private val spriteScalingType: GuiSpriteScaling.Type
	private val atlasSprite: TextureAtlasSprite
	private val color: IntArray = intArrayOf(255, 255, 255, 255)
	private var rotation: Float = 0f

	fun setRotation(rotation: Float): GuiElement {
		this.rotation = rotation
		return this
	}

	/**
	 * Retrieves the actual location of this [GuiElement] if this element is a sprite,
	 * otherwise returns the normal location if this element isn't a sprite.
	 */
	fun actualLocation(withExtension: Boolean = false): ResourceLocation =
		if (this.isSprite) this.location.withPrefix("textures/gui/sprites/")
			.withSuffix(if (withExtension) ".png" else "")
		else this.location

	private fun color(color: Int): Float = this.color[color] / 255f

	private fun getAsStaticTexture(): ResourceLocation =
		this.atlasSprite.contents().name().withPrefix("textures/gui/sprites/").withSuffix(".png")

	init {
		val sprite = localClient.guiSprites.getSprite(this.location)
		if (sprite.contents().name() == MissingTextureAtlasSprite.getLocation()) {
			this.isSprite = false
			this.isAnimatedSprite = false
			this.spriteScalingType = GuiSpriteScaling.DEFAULT.type()
			this.atlasSprite = localClient.guiSprites.getSprite(MissingTextureAtlasSprite.getLocation())
		} else {
			this.isSprite = true
			this.spriteScalingType = sprite.contents().metadata().getSection(GuiMetadataSection.TYPE)
				.orElse(GuiMetadataSection.DEFAULT).scaling.type()
			this.atlasSprite = sprite
			this.isAnimatedSprite = this.atlasSprite.contents().animatedTexture != null
		}
	}

	// todo replace with abstract widget version when i actually turn this class into a widget...
	fun isMouseOver(mouseX: Double, mouseY: Double, x: Int, y: Int): Boolean =
		mouseX >= x
				&& mouseY >= y
				&& mouseX < (x + this.textureWidth)
				&& mouseY < (y + this.textureHeight)

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
	 * Converts this [GuiElement] to a [NativeImage].
	 */
	fun toNativeImage(): NativeImage {
		val stream =
			this::class.java.getResourceAsStream("/assets/${this.location.namespace}/${this.actualLocation(true).path}")!!
		return NativeImage.read(stream)
	}

	/**
	 * Standard blit method for this [GuiElement].
	 *
	 * * scale, width, and height is automatically set based on [textureWidth] and [textureHeight].
	 *
	 * @see blitScaled
	 * @see blitStaticSprite
	 */
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
		val pose = guiGraphics.pose()
		pose.pushPose()
		pose.translate(x, y, 0)
		pose.mulPose(Axis.ZN.rotationDegrees(this.rotation))
		RenderSystem.setShaderColor(this.color(0), this.color(1), this.color(2), this.color(3))
		if (this.isSprite) {
			if (this.spriteScalingType == NINE_SLICE) {
				guiGraphics.blitSprite(this.location, 0, 0, textureWidth, textureHeight)
			} else guiGraphics.blitSprite(
				this.location,
				textureWidth,
				textureHeight,
				uOffset.toInt(),
				vOffset.toInt(),
				0,
				0,
				uWidth,
				vHeight
			)
		} else guiGraphics.blit(this.location, 0, 0, uOffset, vOffset, uWidth, vHeight, textureWidth, textureHeight)
		this.resetColor()
		this.setRotation(0f)
		pose.popPose()
	}

	/**
	 * Blits this [GuiElement] with scaling.
	 *
	 * if this [GuiElement] is a nine-sliced sprite, it will scale accordingly.
	 */
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

	/**
	 * Blits this [GuiElement] as a static sprite.
	 *
	 * Falls back to standard blitting if [isAnimatedSprite] is false.
	 */
	fun blitStaticSprite(guiGraphics: GuiGraphics, x: Int, y: Int, frame: Int = 0) {
		val pose = guiGraphics.pose()
		pose.pushPose()
		pose.translate(x, y, 0)
		pose.mulPose(Axis.ZN.rotationDegrees(this.rotation))
		if (!this.isAnimatedSprite) {
			this.blit(guiGraphics, x, y)
			return
		}
		val location = this.getAsStaticTexture()
		RenderSystem.setShaderColor(this.color(0), this.color(1), this.color(2), this.color(3))
		val contents = this.atlasSprite.contents()
		guiGraphics.blit(
			location,
			0,
			0,
			0f,
			0f + (contents.height() * frame),
			this.textureWidth,
			this.textureHeight,
			contents.width(),
			contents.height() * (contents.uniqueFrames.max().asInt + 1)
		)
		this.resetColor()
		this.setRotation(0f)
		pose.popPose()
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

	override fun equals(other: Any?): Boolean =
		if (other is GuiElement)
			this.location == other.location &&
					this.textureWidth == other.textureWidth &&
					this.textureHeight == other.textureHeight
		else false

	override fun hashCode(): Int = Objects.hash(this.location, this.textureWidth, this.textureHeight)
}
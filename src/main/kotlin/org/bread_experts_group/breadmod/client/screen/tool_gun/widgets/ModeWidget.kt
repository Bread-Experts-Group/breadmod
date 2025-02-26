package org.bread_experts_group.breadmod.client.screen.tool_gun.widgets

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.client.render.texture.BreadModTextureHelper
import java.awt.Color

class ModeWidget(
	val icon: ItemStack,
	val previewImage: BreadModTextureHelper,
	val modeName: Component,
	val modeDescription: Component,
	val id: ResourceLocation
) : AbstractWidget(0, 0, 35, 40, modeName) {
	var isSelected: Boolean = false

	override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		val borderColor = if (this.isSelected) Color.GREEN.rgb else
			if (this.isHovered || this.isFocused) Color(16755200).rgb else Color.GRAY.rgb
		guiGraphics.pose().pushPose()
		guiGraphics.fill(
			RenderType.gui(),
			this.x,
			this.y,
			this.x + 35,
			this.y + 40,
			borderColor
		)
		guiGraphics.fill(RenderType.gui(), this.x + 1, this.y + 1, this.x + 34, this.y + 39, Color.DARK_GRAY.rgb)
		guiGraphics.drawScrollingString(
			localClient.font,
			this.message,
			this.x + 2,
			this.x + 33,
			this.y + 29,
			Color.WHITE.rgb
		)
		guiGraphics.pose().translate(this.x.toFloat() + 5.5f, this.y.toFloat() + 2, 0f)
		guiGraphics.pose().scaleFlat(1.5f)
		guiGraphics.renderFakeItem(this.icon, 0, 0)
		guiGraphics.pose().popPose()
	}

	override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {
		this.defaultButtonNarrationText(narrationElementOutput)
	}

	class Builder {
		private var icon: ItemStack = Items.BARRIER.defaultInstance
		private var previewImage: BreadModTextureHelper = BreadModTextureHelper.MISSING_TEXTURE
		private var modeName: Component? = null
		private var modeDescription: Component? = null
		private var id: ResourceLocation = modLocation()

		fun icon(stack: ItemStack): Builder = this.also { this.icon = stack }
		fun previewImage(
			location: ResourceLocation,
			width: Int,
			height: Int
		): Builder = this.also { this.previewImage(BreadModTextureHelper(location, width, height)) }

		fun previewImage(helper: BreadModTextureHelper): Builder = this.also { this.previewImage = helper }
		fun name(name: Component): Builder = this.also { this.modeName = name }
		fun name(name: String): Builder = this.also { this.name(Component.literal(name)) }
		fun description(description: Component): Builder = this.also { this.modeDescription = description }
		fun description(description: String): Builder =
			this.also { this.description(Component.translatable(description)) }

		fun id(location: ResourceLocation): Builder = this.also { this.id = location }

		fun build(): ModeWidget {
			require(this.id != modLocation()) { "id must be set." }
			require(this.modeName != null) { "mode name component must be set." }
			require(this.modeDescription != null) { "mode description component must be set." }
			return ModeWidget(this.icon, this.previewImage, this.modeName!!, this.modeDescription!!, this.id)
		}
	}
}
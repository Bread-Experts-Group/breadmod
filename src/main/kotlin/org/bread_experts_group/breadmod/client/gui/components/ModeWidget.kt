package org.bread_experts_group.breadmod.client.gui.components

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.gui.components.tool_gun_tabs.ModeSelectTab
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.client.render.texture.BreadModTextureHelper
import org.bread_experts_group.breadmod.util.Selector
import java.awt.Color

/**
 * Widget for holding tool gun mode data.
 * This should only be instantiated in [ModeSelectTab].
 */
class ModeWidget(
	val icon: Selector<ItemStack, BreadModTextureHelper>,
	val previewImage: Selector<ItemStack, BreadModTextureHelper>,
	val modeName: Component,
	val modeDescription: Component,
	val id: ResourceLocation
) : AbstractWidget(0, 0, 35, 40, modeName) {
	var isSelected: Boolean = false
	private val hoverColor: Int = Color(255, 170, 0).rgb

	override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		val borderColor =
			if (this.isSelected) Color.GREEN.rgb
			else if (this.isHovered || this.isFocused) this.hoverColor
			else Color.GRAY.rgb
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
		this.icon.select({
			guiGraphics.renderFakeItem(it, 0, 0)
		}, {
			TODO(it.location.toString())
		})
		guiGraphics.pose().popPose()
	}

	override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {
		this.defaultButtonNarrationText(narrationElementOutput)
	}

	class Builder {
		private var icon: Selector<ItemStack, BreadModTextureHelper> =
			Selector(b = BreadModTextureHelper.BLOCKHEAD_TEXTURE)
		private var previewImage: Selector<ItemStack, BreadModTextureHelper> =
			Selector(b = BreadModTextureHelper.MISSING_TEXTURE)
		private var modeName: Component? = null
		private var modeDescription: Component? = null
		private var id: ResourceLocation = modLocation()

		fun icon(item: Item): Builder = this.icon(item.defaultInstance)
		fun icon(stack: ItemStack): Builder = this.also { this.icon = Selector(a = stack) }

		fun previewImage(helper: BreadModTextureHelper): Builder =
			this.also { this.previewImage = Selector(b = helper) }

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
			return ModeWidget(
				this.icon, this.previewImage,
				this.modeName!!, this.modeDescription!!,
				this.id
			)
		}
	}

	companion object {
		val noWidget: ModeWidget = ModeWidget(
			Selector(b = BreadModTextureHelper.BLOCKHEAD_TEXTURE),
			Selector(b = BreadModTextureHelper.MISSING_TEXTURE),
			Component.literal("???"),
			Component.literal("???"),
			ResourceLocation.fromNamespaceAndPath(BreadMod.ID, "missing")
		)
	}
}
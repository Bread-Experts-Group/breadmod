package org.bread_experts_group.breadmod.client.tool_gun_mode

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.network.serverbound.ToolGunActionPacket
import org.bread_experts_group.breadmod.util.render.localClient
import org.bread_experts_group.breadmod.util.render.scaleFlat
import java.awt.Color

// todo codec for this widget
open class ModeWidget(
	val icon : ItemStack,
	val previewImage : ResourceLocation,
	val modeName : Component,
	val modeDescription : Component
) : AbstractWidget(0, 0, 35, 40, modeName) {
	var namespace : String = ""
	var id : String = ""

	companion object {
		val CODEC : Codec<ModeWidget> = RecordCodecBuilder.create { inst ->
			inst.group(
				ItemStack.CODEC.fieldOf("icon").forGetter(ModeWidget::icon),
				ResourceLocation.CODEC.fieldOf("preview_image").forGetter(ModeWidget::previewImage),
				ComponentSerialization.CODEC.fieldOf("mode_name").forGetter(ModeWidget::modeName),
				ComponentSerialization.CODEC.fieldOf("mode_desc").forGetter(ModeWidget::modeDescription)
			).apply(inst, ::ModeWidget)
		}
		val STREAM_CODEC : StreamCodec<RegistryFriendlyByteBuf, ModeWidget> = StreamCodec.composite(
			ItemStack.STREAM_CODEC, ModeWidget::icon,
			ResourceLocation.STREAM_CODEC, ModeWidget::previewImage,
			ComponentSerialization.STREAM_CODEC, ModeWidget::modeName,
			ComponentSerialization.STREAM_CODEC, ModeWidget::modeDescription,
			::ModeWidget
		)

		val NONE : ModeWidget = ModeWidget(
			ItemStack(Items.BARRIER, 1),
			MissingTextureAtlasSprite.getLocation(),
			Component.literal("Empty Mode"),
			Component.literal("Default Description")
		)
	}

	override fun onClick(mouseX : Double, mouseY : Double, button : Int) {
		PacketDistributor.sendToServer(ToolGunActionPacket(this.namespace, this.id))
		super.onClick(mouseX, mouseY, button)
	}

	override fun renderWidget(guiGraphics : GuiGraphics, mouseX : Int, mouseY : Int, partialTick : Float) {
		guiGraphics.pose().pushPose()
		guiGraphics.fill(
			RenderType.gui(),
			this.x,
			this.y,
			this.x + 35,
			this.y + 40,
			if (this.isHovered || this.isFocused) Color(16755200).rgb else Color.GRAY.rgb
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

	override fun updateWidgetNarration(narrationElementOutput : NarrationElementOutput) {
	}

	class Builder {
		private var icon : ItemStack = ItemStack(Items.BARRIER)
		private var previewImage : ResourceLocation = MissingTextureAtlasSprite.getLocation()
		private var modeName : Component = Component.literal("Default Name")
		private var modeDescription : Component = Component.literal("Default Description")

		fun icon(stack : ItemStack) : Builder = this.also { this.icon = stack }
		fun previewImage(location : ResourceLocation) : Builder = this.also { this.previewImage = location }
		fun name(name : Component) : Builder = this.also { this.modeName = name }
		fun name(name : String) : Builder = this.also { this.name(Component.literal(name)) }
		fun description(description : Component) : Builder = this.also { this.modeDescription = description }
		fun description(description : String) : Builder = this.also { this.description(Component.literal(description)) }

		fun build() : ModeWidget = ModeWidget(
			this.icon,
			this.previewImage,
			this.modeName,
			this.modeDescription
		)
	}
}
package org.bread_experts_group.breadmod.client.tool_gun_mode

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.client.render.texture.BreadModTextureHelper

class ModeWidgetData(
	val icon: ItemStack,
	val previewImage: BreadModTextureHelper,
	val modeName: Component,
	val modeDescription: Component
) {
	var namespace: String = ""
	var name: String = ""

	companion object {
		val CODEC: Codec<ModeWidgetData> = RecordCodecBuilder.create { inst ->
			inst.group(
				ItemStack.CODEC.fieldOf("icon").forGetter(ModeWidgetData::icon),
				BreadModTextureHelper.CODEC.fieldOf("preview_image").forGetter(ModeWidgetData::previewImage),
				ComponentSerialization.CODEC.fieldOf("mode_name").forGetter(ModeWidgetData::modeName),
				ComponentSerialization.CODEC.fieldOf("mode_desc").forGetter(ModeWidgetData::modeDescription)
			).apply(inst, ::ModeWidgetData)
		}
		val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ModeWidgetData> = StreamCodec.composite(
			ItemStack.STREAM_CODEC, ModeWidgetData::icon,
			BreadModTextureHelper.STREAM_CODEC, ModeWidgetData::previewImage,
			ComponentSerialization.STREAM_CODEC, ModeWidgetData::modeName,
			ComponentSerialization.STREAM_CODEC, ModeWidgetData::modeDescription,
			::ModeWidgetData
		)
		val NONE: ModeWidgetData = ModeWidgetData(
			ItemStack(Items.BARRIER, 1),
			BreadModTextureHelper.MISSING_TEXTURE,
			modTranslatable("tool_gun", "empty", "mode", "name"),
			modTranslatable("tool_gun", "empty", "mode", "description")
		)
	}

	class Builder {
		private var icon: ItemStack = Items.BARRIER.defaultInstance
		private var previewImage: BreadModTextureHelper = BreadModTextureHelper.MISSING_TEXTURE
		private var modeName: Component = modTranslatable("tool_gun", "default", "mode", "name")
		private var modeDescription: Component = modTranslatable("tool_gun", "default", "mode", "description")

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

		fun buildData(): ModeWidgetData = ModeWidgetData(
			this.icon,
			this.previewImage,
			this.modeName,
			this.modeDescription
		)
	}
}
package org.bread_experts_group.breadmod.client.tool_gun_mode

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.bread_experts_group.breadmod.client.render.texture.BreadModTextureHelper

class ModeWidgetData(
	val icon: ItemStack,
	val previewImage: BreadModTextureHelper,
	val modeName: Component,
	val modeDescription: Component
) {
	var namespace: String = ""
	var id: String = ""

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
			Component.literal("Empty Mode"),
			Component.literal("Default Description")
		)
	}
}
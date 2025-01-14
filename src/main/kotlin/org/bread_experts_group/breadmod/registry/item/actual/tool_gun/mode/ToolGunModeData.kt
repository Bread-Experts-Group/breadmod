package org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.codec.StreamCodec
import org.bread_experts_group.breadmod.client.tool_gun_mode.ModeWidgetData

data class ToolGunModeData(
	val namespaceAndName: Pair<String, String>,
	val displayName: Component,
	val tooltip: Component,
	val actionClass: ToolGunMode,
	val widgetData: ModeWidgetData
) {
	init {
		this.widgetData.namespace = this.namespaceAndName.first
		this.widgetData.name = this.namespaceAndName.second
	}

	companion object {
		private val pairStreamCodec: StreamCodec<FriendlyByteBuf, Pair<String, String>> =
			object : StreamCodec<FriendlyByteBuf, Pair<String, String>> {
				override fun decode(buffer: FriendlyByteBuf): Pair<String, String> =
					Pair(buffer.readUtf(), buffer.readUtf())

				override fun encode(buffer: FriendlyByteBuf, value: Pair<String, String>) {
					buffer.writeUtf(value.first)
					buffer.writeUtf(value.second)
				}
			}
		val CODEC: Codec<ToolGunModeData> = RecordCodecBuilder.create { inst ->
			inst.group(
				Codec.pair(
					Codec.STRING.fieldOf("namespace").codec(),
					Codec.STRING.fieldOf("name").codec()
				).fieldOf("id").forGetter(ToolGunModeData::namespaceAndName),
				ComponentSerialization.CODEC.fieldOf("display_name").forGetter(ToolGunModeData::displayName),
				ComponentSerialization.CODEC.fieldOf("tooltip").forGetter(ToolGunModeData::tooltip),
				ToolGunMode.CODEC.fieldOf("action_class").forGetter(ToolGunModeData::actionClass),
				ModeWidgetData.CODEC.fieldOf("widget_data").forGetter(ToolGunModeData::widgetData)
			).apply(inst, ::ToolGunModeData)
		}
		val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ToolGunModeData> = StreamCodec.composite(
			this.pairStreamCodec, ToolGunModeData::namespaceAndName,
			ComponentSerialization.STREAM_CODEC, ToolGunModeData::displayName,
			ComponentSerialization.STREAM_CODEC, ToolGunModeData::tooltip,
			ToolGunMode.STREAM_CODEC, ToolGunModeData::actionClass,
			ModeWidgetData.STREAM_CODEC, ToolGunModeData::widgetData,
			::ToolGunModeData
		)
		val EMPTY: ToolGunModeData = ToolGunModeData(
			Pair("breadmod", "empty"),
			Component.literal("???"),
			Component.literal("If you see this mode then something probably went wrong!"),
			EmptyMode(),
			ModeWidgetData.NONE
		)
	}
}
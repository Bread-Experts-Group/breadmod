package org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import org.bread_experts_group.breadmod.client.tool_gun_mode.ModeWidget

data class ToolGunModeData(
	val namespace: String,
	val name: String,
	val displayName: Component,
	val tooltip: Component,
	val actionClass: ToolGunMode,
	val widget: ModeWidget
) {
	init {
		this.widget.namespace = this.namespace
		this.widget.id = this.name
	}

	companion object {
		val CODEC: Codec<ToolGunModeData> = RecordCodecBuilder.create { inst ->
			inst.group(
				Codec.STRING.fieldOf("namespace").forGetter(ToolGunModeData::namespace),
				Codec.STRING.fieldOf("name").forGetter(ToolGunModeData::name),
				ComponentSerialization.CODEC.fieldOf("display_name").forGetter(ToolGunModeData::displayName),
				ComponentSerialization.CODEC.fieldOf("tooltip").forGetter(ToolGunModeData::tooltip),
				ToolGunMode.CODEC.fieldOf("action_class").forGetter(ToolGunModeData::actionClass),
				ModeWidget.CODEC.fieldOf("widget").forGetter(ToolGunModeData::widget)
			).apply(inst, ::ToolGunModeData)
		}
		val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ToolGunModeData> = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, ToolGunModeData::namespace,
			ByteBufCodecs.STRING_UTF8, ToolGunModeData::name,
			ComponentSerialization.STREAM_CODEC, ToolGunModeData::displayName,
			ComponentSerialization.STREAM_CODEC, ToolGunModeData::tooltip,
			ToolGunMode.STREAM_CODEC, ToolGunModeData::actionClass,
			ModeWidget.STREAM_CODEC, ToolGunModeData::widget,
			::ToolGunModeData
		)
		val EMPTY: ToolGunModeData = ToolGunModeData(
			"breadmod",
			"empty",
			Component.literal("???"),
			Component.literal("If you see this mode then something probably went wrong!"),
			EmptyMode(),
			ModeWidget.NONE
		)
	}
}
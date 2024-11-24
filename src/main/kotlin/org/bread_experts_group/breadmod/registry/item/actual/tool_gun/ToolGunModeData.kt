package org.bread_experts_group.breadmod.registry.item.actual.tool_gun

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec

data class ToolGunModeData(
    var namespace: String,
    var name: String,
    var namespaceIteratorState: Int,
    var modeIteratorState: Int
) {
    companion object {
        val EMPTY = ToolGunModeData("breadmod", "none", 0, 0)

        val CODEC: Codec<ToolGunModeData> = RecordCodecBuilder.create { inst ->
            inst.group(
                Codec.STRING.fieldOf("namespace").forGetter(ToolGunModeData::namespace),
                Codec.STRING.fieldOf("name").forGetter(ToolGunModeData::name),
                Codec.INT.fieldOf("namespace_iterator_state").forGetter(ToolGunModeData::namespaceIteratorState),
                Codec.INT.fieldOf("mode_iterator_state").forGetter(ToolGunModeData::modeIteratorState)
            ).apply(inst, ::ToolGunModeData)
        }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ToolGunModeData> = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ToolGunModeData::namespace,
            ByteBufCodecs.STRING_UTF8, ToolGunModeData::name,
            ByteBufCodecs.INT, ToolGunModeData::namespaceIteratorState,
            ByteBufCodecs.INT, ToolGunModeData::modeIteratorState,
            ::ToolGunModeData
        )
    }

    fun resetIteratorStates() {
        namespaceIteratorState = 0
        modeIteratorState = 0
    }
}
package org.bread_experts_group.breadmod.network.clientbound

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.network.handling.IPayloadContext
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.datagen.tool_gun.ToolGunModeDataLoader

class ToolGunModeDataPacket(
    val location: ResourceLocation,
    val data: ByteArray,
    val done: Boolean
) : CustomPacketPayload {
    companion object {
        val TYPE: CustomPacketPayload.Type<ToolGunModeDataPacket> =
            CustomPacketPayload.Type(modLocation("mode_data_packet"))

        val STREAM_CODEC: StreamCodec<ByteBuf, ToolGunModeDataPacket> = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, ToolGunModeDataPacket::location,
            ByteBufCodecs.BYTE_ARRAY, ToolGunModeDataPacket::data,
            ByteBufCodecs.BOOL, ToolGunModeDataPacket::done,
            ::ToolGunModeDataPacket
        )

        fun handleClientboundPacket(data: ToolGunModeDataPacket, context: IPayloadContext) {
            context.enqueueWork {
                println(data.data.decodeToString())
                toLoad[data.location] = gson.fromJson(data.data.decodeToString(), JsonObject::class.java)
                if (data.done) {
                    ToolGunModeDataLoader.load(toLoad)
                    ToolGunModeDataLoader.loadKeys()
                }
            }
        }

        private val gson = Gson()
        private val toLoad: MutableMap<ResourceLocation, JsonElement> = mutableMapOf()
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE
}
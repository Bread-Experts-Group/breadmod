package bread.mod.breadmod.networking.definition

import bread.mod.breadmod.ModMainCommon.modLocation
import bread.mod.breadmod.util.MachTrailData
import bread.mod.breadmod.util.render.machTrailMap
import com.mojang.authlib.GameProfile
import dev.architectury.networking.NetworkManager.NetworkReceiver
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

internal data class MachTrailPacket(
    val playerProfile: GameProfile
) : CustomPacketPayload {
    companion object {
        val TYPE: CustomPacketPayload.Type<MachTrailPacket> =
            CustomPacketPayload.Type(modLocation("mach_trail_packet"))

        val STREAM_CODEC: StreamCodec<ByteBuf, MachTrailPacket> = StreamCodec.composite(
            ByteBufCodecs.GAME_PROFILE, MachTrailPacket::playerProfile
        ) { profile -> MachTrailPacket(profile) }

        val RECEIVER = NetworkReceiver<MachTrailPacket> { (playerProfile), _ ->
            machTrailMap[playerProfile] = MachTrailData(playerProfile)
        }
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE
}
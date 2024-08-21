package breadmod.network.common.tool_gun.creator

import breadmod.ModMain.modTranslatable
import breadmod.client.screen.tool_gun.ToolGunCreatorScreen.Companion.loadedEntity
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.TOOL_GUN_DEF
import breadmod.item.tool_gun.mode.creator.ToolGunCreatorMode
import breadmod.network.PacketHandler.NETWORK
import breadmod.util.json
import kotlinx.serialization.encodeToString
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

internal class ToolGunCreatorDataRequestPacket(val savedHash: Int, val json: String?) {
    companion object {
        val requested = mutableMapOf<UUID, (ToolGunCreatorMode.CreatorEntity) -> Unit>()
        val data = mutableMapOf<UUID, ToolGunCreatorMode.CreatorEntity>()

        fun encodeBuf(input: ToolGunCreatorDataRequestPacket, buffer: FriendlyByteBuf) {
            buffer.writeVarInt(input.savedHash)
            buffer.writeNullable(input.json) { buf, data -> buf.writeUtf(data) }
        }

        fun decodeBuf(input: FriendlyByteBuf): ToolGunCreatorDataRequestPacket = ToolGunCreatorDataRequestPacket(
            input.readVarInt(),
            input.readNullable { it.readUtf() }
        )

        fun handle(input: ToolGunCreatorDataRequestPacket, ctx: Supplier<NetworkEvent.Context>) = ctx.get().let {
            val player = it.sender
            if (player != null) {
                // Server
                if (requested.contains(player.uuid)) {
                    val removed = requested.remove(player.uuid)

                    val json = input.json
                    if (json != null) {
                        val decoded: ToolGunCreatorMode.CreatorEntity = breadmod.util.json.decodeFromString(input.json)
                        data[player.uuid] = decoded
                    }

                    (removed ?: return@let).invoke(data[player.uuid] ?: return@let)
                } else player.connection.disconnect(modTranslatable("item", TOOL_GUN_DEF, "creator", "bad_timing"))
            } else {
                // Client
                val encoded = json.encodeToString(loadedEntity)
                NETWORK.sendToServer(
                    ToolGunCreatorDataRequestPacket(
                        0,
                        if (input.savedHash != encoded.hashCode()) encoded
                        else null
                    )
                )
            }
            it.packetHandled = true
        }
    }
}
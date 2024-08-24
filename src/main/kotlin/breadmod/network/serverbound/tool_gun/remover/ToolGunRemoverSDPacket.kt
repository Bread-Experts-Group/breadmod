package breadmod.network.serverbound.tool_gun.remover

import breadmod.ModMain.modTranslatable
import breadmod.item.tool_gun.ToolGunItem
import breadmod.network.PacketHandler.NETWORK
import breadmod.util.computerSD
import breadmod.util.render.rgMinecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.InteractionHand
import net.minecraftforge.network.NetworkEvent
import net.minecraftforge.network.PacketDistributor
import java.util.*
import java.util.function.Supplier

internal data class ToolGunRemoverSDPacket(val playerUUID: UUID?) {
    companion object {
        fun encodeBuf(input: ToolGunRemoverSDPacket, buffer: FriendlyByteBuf) {
            buffer.writeNullable(input.playerUUID) { a, b -> a.writeUUID(b) }
        }

        fun decodeBuf(input: FriendlyByteBuf) = ToolGunRemoverSDPacket(input.readNullable { it.readUUID() })

        fun handle(input: ToolGunRemoverSDPacket, ctx: Supplier<NetworkEvent.Context>) = ctx.get().let {
            val sender = it.sender
            if (sender != null) {
                if (sender.getItemInHand(InteractionHand.MAIN_HAND).item is ToolGunItem) {
                    val toDC = sender.server.playerList.players.firstOrNull { p -> p.uuid == input.playerUUID } ?: return
                    NETWORK.send(PacketDistributor.PLAYER.with { toDC }, ToolGunRemoverSDPacket(null))
                }
            } else {
                rgMinecraft.connection?.connection?.disconnect(modTranslatable("item", "tool_gun", "player_left_game"))
                computerSD(false)
            }
            it.packetHandled = true
        }
    }
}
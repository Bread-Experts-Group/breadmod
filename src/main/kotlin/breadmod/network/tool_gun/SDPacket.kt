package breadmod.network.tool_gun

import breadmod.ModMain.modTranslatable
import breadmod.item.tool_gun.ToolGunItem
import breadmod.network.PacketHandler.NETWORK
import breadmod.util.computerSD
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.InteractionHand
import net.minecraftforge.network.NetworkEvent
import net.minecraftforge.network.PacketDistributor
import java.util.UUID
import java.util.function.Supplier

data class SDPacket(val playerUUID: UUID?) {
    companion object {
        fun encodeBuf(input: SDPacket, buffer: FriendlyByteBuf) { buffer.writeNullable(input.playerUUID) { a, b -> a.writeUUID(b) } }
        fun decodeBuf(input: FriendlyByteBuf) = SDPacket(input.readNullable { it.readUUID() })

        fun handle(input: SDPacket, ctx: Supplier<NetworkEvent.Context>) = ctx.get().let {
            val sender = it.sender
            if(sender != null) {
                if(sender.getItemInHand(InteractionHand.MAIN_HAND).item is ToolGunItem) {
                    val toDC = sender.server.playerList.players.firstOrNull { it.uuid == input.playerUUID } ?: return
                    NETWORK.send(PacketDistributor.PLAYER.with { toDC }, SDPacket(null))
                }
            } else {
                val minecraft = Minecraft.getInstance()
                minecraft.connection?.connection?.disconnect(modTranslatable("item", "tool_gun", "player_left_game"))
                computerSD(false)
            }
            it.packetHandled = true
        }
    }
}
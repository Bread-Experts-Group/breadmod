package breadmod.network.tool_gun

import breadmod.item.tool_gun.mode.creator.ToolGunCreatorMode
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class ToolGunCreatorDataPacket(val data: String) {
    companion object {
        fun encodeBuf(input: ToolGunCreatorDataPacket, buffer: FriendlyByteBuf) {
            buffer.writeUtf(input.data)
        }
        fun decodeBuf(input: FriendlyByteBuf): ToolGunCreatorDataPacket =
            ToolGunCreatorDataPacket(input.readUtf())

        fun handle(input: ToolGunCreatorDataPacket, ctx: Supplier<NetworkEvent.Context>) = ctx.get().let { context ->
            context.enqueueWork {
                ToolGunCreatorMode.objects[context.sender ?: throw IllegalStateException("Server sent server-bound packet???")] = input.data
            }
            context.packetHandled = true
        }
    }
}
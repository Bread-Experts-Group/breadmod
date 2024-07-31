package breadmod.network.tool_gun

import breadmod.client.render.tool_gun.ToolGunAnimationHandler
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class ToolGunAnimationPacket(val self: Boolean) {
    companion object {
        fun encodeBuf(input: ToolGunAnimationPacket, buffer: FriendlyByteBuf) {
            buffer.writeBoolean(input.self)
        }
        fun decodeBuf(input: FriendlyByteBuf) =
            ToolGunAnimationPacket(input.readBoolean())
        fun handle(input: ToolGunAnimationPacket, ctx: Supplier<NetworkEvent.Context>) = ctx.get().let {
            it.enqueueWork {
                if(input.self) ToolGunAnimationHandler.trigger()
            }
            it.packetHandled = true
        }
    }
}
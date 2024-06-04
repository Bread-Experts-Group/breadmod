package breadmod.network

import breadmod.util.readVec3
import breadmod.util.render.drawLine
import breadmod.util.writeVec3
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.phys.Vec3
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

data class BeamPacket(val pStart: Vec3, val pEnd: Vec3, val thickness: Float) {
    companion object {
        fun encodeBuf(input: BeamPacket, buffer: FriendlyByteBuf) {
            buffer.writeVec3(input.pStart).writeVec3(input.pEnd).writeFloat(input.thickness) }
        fun decodeBuf(input: FriendlyByteBuf): BeamPacket =
            BeamPacket(input.readVec3(), input.readVec3(), input.readFloat())

        fun handle(input: BeamPacket, ctx: Supplier<NetworkEvent.Context>) = ctx.get().let {
            if(it.sender == null) {
                drawLine(input.pStart, input.pEnd, input.thickness)
                it.packetHandled = true
            }
        }
    }
}
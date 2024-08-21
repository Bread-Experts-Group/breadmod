package breadmod.network.clientbound

import breadmod.util.render.addBeamTask
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import org.joml.Vector3f
import java.util.function.Supplier

/**
 * Client-bound packet to draw a line between [pStart] and [pEnd].
 *
 * @author Miko Elbrecht
 * @since 1.0.0
 */
internal data class BeamPacket(val pStart: Vector3f, val pEnd: Vector3f, val thickness: Float?) {
    companion object {
        fun encodeBuf(input: BeamPacket, buffer: FriendlyByteBuf) {
            buffer.writeVector3f(input.pStart)
            buffer.writeVector3f(input.pEnd)
            buffer.writeNullable(input.thickness) { cons, vr -> cons.writeFloat(vr) }
        }

        fun decodeBuf(input: FriendlyByteBuf): BeamPacket =
            BeamPacket(input.readVector3f(), input.readVector3f(), input.readNullable { it.readFloat() })

        fun handle(input: BeamPacket, ctx: Supplier<NetworkEvent.Context>) = ctx.get().let {
            if (it.sender == null) {
                addBeamTask(input.pStart, input.pEnd, input.thickness)
                it.packetHandled = true
            }
        }
    }
}
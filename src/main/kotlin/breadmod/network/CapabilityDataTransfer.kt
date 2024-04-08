package breadmod.network

import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier


// TODO Better impl
data class CapabilityDataTransfer(val pPos: BlockPos, val currentEnergy: Int) {
    companion object {
        fun encodeBuf(input: CapabilityDataTransfer, buffer: FriendlyByteBuf) {
            buffer.writeBlockPos(input.pPos).writeInt(input.currentEnergy) }
        fun decodeBuf(input: FriendlyByteBuf): CapabilityDataTransfer =
            CapabilityDataTransfer(input.readBlockPos(), input.readInt())

        fun handle(input: CapabilityDataTransfer, ctx: Supplier<NetworkEvent.Context>) = ctx.get().let {
            it.enqueueWork {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT) { Runnable {

                }}
            }
            it.packetHandled = true
        }
    }
}
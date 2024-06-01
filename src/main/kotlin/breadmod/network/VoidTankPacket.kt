package breadmod.network

import breadmod.util.capability.FluidContainer
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

data class VoidTankPacket(val pPos: BlockPos, val index: Int) {
    companion object {
        fun encodeBuf(input: VoidTankPacket, buffer: FriendlyByteBuf) {
            buffer.writeBlockPos(input.pPos).writeInt(input.index) }
        fun decodeBuf(input: FriendlyByteBuf): VoidTankPacket =
            VoidTankPacket(input.readBlockPos(), input.readInt())

        fun handle(input: VoidTankPacket, ctx: Supplier<NetworkEvent.Context>) = ctx.get().let {
            it.enqueueWork {
                it.sender?.let { player ->
                    println("${player.name}")
                    val level = player.level()
                    if(level.hasChunkAt(input.pPos)) {
                        level.getBlockEntity(input.pPos)?.getCapability(ForgeCapabilities.FLUID_HANDLER)?.ifPresent { fluidHandle ->
                            if(fluidHandle is FluidContainer) fluidHandle.allTanks[input.index].fluid.amount = 0
                        }
                    }
                }
            }
            it.packetHandled = true
        }
    }
}
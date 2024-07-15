package breadmod.network

import breadmod.block.machine.entity.AbstractMachineBlockEntity
import breadmod.util.capability.FluidContainer
import net.minecraft.core.BlockPos
import net.minecraft.core.SectionPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.network.NetworkEvent
import net.minecraftforge.network.PacketDistributor
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
                    val level = player.level()
                    val pChunkX = SectionPos.blockToSectionCoord(input.pPos.x)
                    val pChunkZ = SectionPos.blockToSectionCoord(input.pPos.z)
                    if(level.hasChunk(pChunkX, pChunkZ)) {
                        (level.getBlockEntity(input.pPos) as? AbstractMachineBlockEntity<*>)?.let { entity ->
                            entity.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent { fluidHandle ->
                                if(fluidHandle is FluidContainer) {
                                    fluidHandle.allTanks[input.index].fluid.amount = 0
                                    PacketDistributor.TRACKING_CHUNK.with { level.getChunk(pChunkX, pChunkZ) }
                                        .send(entity.getUpdatePacket())
                                }
                            }
                        }
                    }
                }
            }
            it.packetHandled = true
        }
    }
}
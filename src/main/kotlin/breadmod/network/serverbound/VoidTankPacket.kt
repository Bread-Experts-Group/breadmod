package breadmod.network.serverbound

import breadmod.block.entity.machine.AbstractMachineBlockEntity
import breadmod.util.capability.FluidContainer
import net.minecraft.core.BlockPos
import net.minecraft.core.SectionPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.network.NetworkEvent
import net.minecraftforge.network.PacketDistributor
import java.util.function.Supplier

/**
 * Packet to drain a tank at a specified [pIndex] in a [FluidContainer], in a block located at [pPos].
 *
 * @author Miko Elbrecht
 * @since 1.0.0
 */
internal data class VoidTankPacket(
    /**
     * Block position of the [AbstractMachineBlockEntity] to drain the tank from.
     *
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    val pPos: BlockPos,
    /**
     * Index of the tank to drain in the [FluidContainer].
     *
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    val pIndex: Int
) {
    companion object {
        /**
         * Function to encode this [VoidTankPacket] into a [FriendlyByteBuf].
         *
         * @author Miko Elbrecht
         * @since 1.0.0
         */
        fun encodeBuf(input: VoidTankPacket, buffer: FriendlyByteBuf) {
            buffer.writeBlockPos(input.pPos).writeInt(input.pIndex)
        }

        /**
         * Function to decode a [VoidTankPacket] from a [FriendlyByteBuf].
         *
         * @author Miko Elbrecht
         * @since 1.0.0
         */
        fun decodeBuf(input: FriendlyByteBuf): VoidTankPacket = VoidTankPacket(input.readBlockPos(), input.readInt())

        /**
         * Function to handle a [VoidTankPacket].
         * Drains the [FluidContainer] tank at [VoidTankPacket.pIndex], at block [VoidTankPacket.pPos].
         *
         * @author Miko Elbrecht
         * @since 1.0.0
         */
        fun handle(input: VoidTankPacket, ctx: Supplier<NetworkEvent.Context>): Unit = ctx.get().let {
            it.enqueueWork {
                it.sender?.let { player ->
                    val level = player.level()

                    val pChunkX = SectionPos.blockToSectionCoord(input.pPos.x)
                    val pChunkZ = SectionPos.blockToSectionCoord(input.pPos.z)

                    if (level.hasChunk(pChunkX, pChunkZ)) {
                        (level.getBlockEntity(input.pPos) as? AbstractMachineBlockEntity<*>)?.let { entity ->
                            entity.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent { fluidHandle ->
                                if (fluidHandle is FluidContainer) {
                                    fluidHandle.allTanks[input.pIndex].fluid.amount = 0
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
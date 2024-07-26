package breadmod.network.server

import breadmod.block.machine.entity.AbstractMachineBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.SectionPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

/**
 * Packet to toggle the [BlockStateProperties.ENABLED] state of an [AbstractMachineBlockEntity],
 * disabling ticking.
 *
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class ToggleMachinePacket(
    /**
     * Block position of the [AbstractMachineBlockEntity] to toggle.
     *
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    val pPos: BlockPos
) {
    companion object {
        /**
         * Function to encode this [ToggleMachinePacket] into a [FriendlyByteBuf].
         *
         * @author Miko Elbrecht
         * @since 1.0.0
         */
        fun encodeBuf(input: ToggleMachinePacket, buffer: FriendlyByteBuf) {
            buffer.writeBlockPos(input.pPos)
        }

        /**
         * Function to decode a [ToggleMachinePacket] from a [FriendlyByteBuf].
         *
         * @author Miko Elbrecht
         * @since 1.0.0
         */
        fun decodeBuf(input: FriendlyByteBuf): ToggleMachinePacket = ToggleMachinePacket(input.readBlockPos())

        /**
         * Function to handle a [ToggleMachinePacket].
         * Toggles the [BlockStateProperties.ENABLED] on the [AbstractMachineBlockEntity] at [ToggleMachinePacket.pPos].
         *
         * @author Miko Elbrecht
         * @since 1.0.0
         */
        fun handle(input: ToggleMachinePacket, ctx: Supplier<NetworkEvent.Context>): Unit = ctx.get().let {
            it.enqueueWork {
                it.sender?.let { player ->
                    val level = player.level()

                    val pChunkX = SectionPos.blockToSectionCoord(input.pPos.x)
                    val pChunkZ = SectionPos.blockToSectionCoord(input.pPos.z)

                    if (level.hasChunk(pChunkX, pChunkZ)) {
                        val entity = level.getBlockEntity(input.pPos)
                        if (entity != null && entity is AbstractMachineBlockEntity<*>) {
                            val state = entity.getBlockState()
                            state.setValue(BlockStateProperties.ENABLED, !state.getValue(BlockStateProperties.ENABLED))
                        }
                    }
                }
            }
            it.packetHandled = true
        }
    }
}
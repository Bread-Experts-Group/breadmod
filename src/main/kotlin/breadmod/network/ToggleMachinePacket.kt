package breadmod.network

import breadmod.block.machine.entity.AbstractMachineBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class ToggleMachinePacket(val pPos: BlockPos, val index: Boolean) {
    companion object {
        fun encodeBuf(input: ToggleMachinePacket, buffer: FriendlyByteBuf) {
            buffer.writeBlockPos(input.pPos).writeBoolean(input.index) }
        fun decodeBuf(input: FriendlyByteBuf): ToggleMachinePacket =
            ToggleMachinePacket(input.readBlockPos(), input.readBoolean())

        fun handle(input: ToggleMachinePacket, ctx: Supplier<NetworkEvent.Context>) = ctx.get().let {
            it.enqueueWork {
                it.sender?.let { player ->
                    println("${player.name}")
                    val level = player.level()
                    @Suppress("DEPRECATION")
                    if(level.hasChunkAt(input.pPos)) {
                        val entity = level.getBlockEntity(input.pPos)
                        if(entity != null && entity is AbstractMachineBlockEntity<*>) {
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
package breadmod.network.serverbound

import breadmod.block.entity.SoundBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.SectionPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

internal data class SoundBlockPacket(
    val pPos: BlockPos,
    val pString: String
) {
    companion object {
        fun encodeBuf(input: SoundBlockPacket, buffer: FriendlyByteBuf) {
            buffer.writeBlockPos(input.pPos).writeUtf(input.pString)
        }

        fun decodeBuf(input: FriendlyByteBuf): SoundBlockPacket =
            SoundBlockPacket(input.readBlockPos(), input.readUtf())

        fun handle(input: SoundBlockPacket, ctx: Supplier<NetworkEvent.Context>) = ctx.get().let {
            it.enqueueWork {
                it.sender?.let { player ->
                    val level = player.level()

                    val chunkX = SectionPos.blockToSectionCoord(input.pPos.x)
                    val chunkZ = SectionPos.blockToSectionCoord(input.pPos.z)

                    if (level.hasChunk(chunkX, chunkZ)) {
                        (level.getBlockEntity(input.pPos) as? SoundBlockEntity)?.let { entity ->
                            entity.currentSound = input.pString
                        }
                    }
                }
            }
        }
    }
}
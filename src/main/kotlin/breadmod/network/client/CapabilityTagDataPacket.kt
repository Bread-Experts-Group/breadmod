package breadmod.network.client

import breadmod.util.render.minecraft
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

data class CapabilityTagDataPacket(val pPos: BlockPos, val tag: CompoundTag?) {
    companion object {
        fun encodeBuf(input: CapabilityTagDataPacket, buffer: FriendlyByteBuf) {
            buffer.writeBlockPos(input.pPos).writeNbt(input.tag)
        }

        fun decodeBuf(input: FriendlyByteBuf): CapabilityTagDataPacket =
            CapabilityTagDataPacket(input.readBlockPos(), input.readAnySizeNbt())

        fun handle(input: CapabilityTagDataPacket, ctx: Supplier<NetworkEvent.Context>) = ctx.get().let {
            it.enqueueWork {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT) {
                    Runnable {
                        if (input.tag != null)
                            minecraft.level?.let { level -> level.getBlockEntity(input.pPos)?.load(input.tag) }
                    }
                }
            }
            it.packetHandled = true
        }
    }
}
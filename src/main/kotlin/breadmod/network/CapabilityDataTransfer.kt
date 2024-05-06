package breadmod.network

import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

data class CapabilityDataTransfer(val pPos: BlockPos, val tag: CompoundTag?) {
    companion object {
        fun encodeBuf(input: CapabilityDataTransfer, buffer: FriendlyByteBuf) {
            buffer.writeBlockPos(input.pPos).writeNbt(input.tag) }
        fun decodeBuf(input: FriendlyByteBuf): CapabilityDataTransfer =
            CapabilityDataTransfer(input.readBlockPos(), input.readAnySizeNbt())

        fun handle(input: CapabilityDataTransfer, ctx: Supplier<NetworkEvent.Context>) = ctx.get().let {
            it.enqueueWork {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT) { Runnable {
                    if(input.tag != null)
                        Minecraft.getInstance().level?.let { level -> level.getBlockEntity(input.pPos)?.load(input.tag) }
                }}
            }
            it.packetHandled = true
        }
    }
}
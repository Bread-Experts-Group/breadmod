package breadmod.network.clientbound

import breadmod.block.entity.machine.AbstractMachineBlockEntity
import breadmod.util.render.rgMinecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

internal data class CapabilitySideDataPacket(
    val pPos: BlockPos,
    val pCapability: String,
    val pSides: List<Direction?>
) {
    companion object {
        fun encodeBuf(input: CapabilitySideDataPacket, buffer: FriendlyByteBuf) {
            buffer.writeBlockPos(input.pPos).writeUtf(input.pCapability).writeInt(input.pSides.size)
            repeat(input.pSides.size) {
                buffer.writeEnum(
                    input.pSides[it] ?: throw IllegalStateException("Direction is not an enum??")
                )
            }
        }

        fun decodeBuf(input: FriendlyByteBuf): CapabilitySideDataPacket =
            CapabilitySideDataPacket(input.readBlockPos(), input.readUtf(), buildList(input.readInt()) {
                add(input.readEnum(Direction::class.java))
            })

        fun handle(input: CapabilitySideDataPacket, ctx: Supplier<NetworkEvent.Context>) = ctx.get().let { context ->
            context.enqueueWork {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT) {
                    Runnable {
                        (rgMinecraft.level?.getBlockEntity(input.pPos) as? AbstractMachineBlockEntity<*>)
                            ?.capabilityHolder?.let {
                                it.capabilities.keys.firstOrNull { c -> c.name == input.pCapability }?.let { cap ->
                                    it.capabilities[cap]?.second?.let { sides ->
                                        sides.clear()
                                        sides.addAll(input.pSides)
                                    }
                                }
                            }
                    }
                }
            }
            context.packetHandled = true
        }
    }
}
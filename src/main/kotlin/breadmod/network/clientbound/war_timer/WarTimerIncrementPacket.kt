package breadmod.network.clientbound.war_timer

import breadmod.client.gui.WarTickerClient
import breadmod.registry.sound.ModSounds
import breadmod.util.render.rgMinecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class WarTimerIncrementPacket(val isIncreasing: Boolean, val increaseTimer: Int) {
    companion object {
        fun encodeBuf(input: WarTimerIncrementPacket, buffer: FriendlyByteBuf) {
            buffer.writeBoolean(input.isIncreasing)
                .writeInt(input.increaseTimer)
        }

        fun decodeBuf(input: FriendlyByteBuf): WarTimerIncrementPacket =
            WarTimerIncrementPacket(input.readBoolean(), input.readInt())

        fun handle(input: WarTimerIncrementPacket, ctx: Supplier<NetworkEvent.Context>) = ctx.get().let {
            it.enqueueWork {
                val player = rgMinecraft.player ?: return@enqueueWork
                WarTickerClient.isTimerIncreasing = input.isIncreasing
                WarTickerClient.increasingTimer = input.increaseTimer
                player.playSound(ModSounds.WAR_TIMER_UP.get(), 0.7f, 1.0f)
            }
        }
    }
}
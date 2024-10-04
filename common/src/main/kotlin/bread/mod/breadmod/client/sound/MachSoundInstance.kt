package bread.mod.breadmod.client.sound

import bread.mod.breadmod.util.render.rgMinecraft
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.player.Player

class MachSoundInstance(
    soundEvent: SoundEvent,
    private val range: IntRange,
    private val player: Player?,
    var timer: Int = 0
) : AbstractTickableSoundInstance(soundEvent, SoundSource.AMBIENT, SoundInstance.createUnseededRandom()) {
    private var stopped = false
    var shouldLoop = false

    init {
        delay = 0
    }

    override fun tick() {
        val currentPlayer = player ?: rgMinecraft.player ?: return
        if (!currentPlayer.isRemoved && timer in range && !stopped) {
            stopped = false
            looping = true
            x = currentPlayer.x
            y = currentPlayer.y
            z = currentPlayer.z
        } else stop()
    }

    override fun isStopped(): Boolean = stopped

    override fun isLooping(): Boolean = shouldLoop
}
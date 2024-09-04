package breadmod.block

import breadmod.CommonForgeEventBus.warTimerMap
import breadmod.network.PacketHandler
import breadmod.network.clientbound.war_timer.WarTimerIncrementPacket
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.FluidState
import net.minecraftforge.network.PacketDistributor

// todo 3d model, texturing, sounds
class WarTerminalBlock: Block(Properties.of()) {

    override fun onDestroyedByPlayer(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        willHarvest: Boolean,
        fluid: FluidState
    ): Boolean {
        val server = level.server ?: return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid)
        server.playerList.players.forEach { player ->
            warTimerMap[player]?.let {
                val increase = it.first.third + 30
                warTimerMap.put(player, Triple(it.first.first, 41, increase) to (it.second.first to true))
                PacketHandler.NETWORK.send(
                    PacketDistributor.PLAYER.with { player },
                    WarTimerIncrementPacket(true, increase)
                )
            }
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid)
    }
}
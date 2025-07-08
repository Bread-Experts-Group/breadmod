package org.bread_experts_group.breadmod.registry.block.actual.entity

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadmod.client.render.LerpTicker
import org.bread_experts_group.breadmod.client.render.LerpTicker.LerpParams
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.sound.StereoSoundInstance
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.util.component1
import org.bread_experts_group.breadmod.util.component2
import org.bread_experts_group.breadmod.util.component3
import java.lang.Math.clamp
import kotlin.math.PI
import kotlin.math.atan

class RadioBlockEntity(
	pos: BlockPos,
	state: BlockState,
) : BreadModBlockEntity<RadioBlockEntity>(ModBlockEntityTypes.RADIO.get(), pos, state), LerpTicker {
	var displayFlip: Float = -90f
	override val lerpParams: Array<LerpTicker.LerpParams> = arrayOf(LerpParams(), LerpParams())

	companion object {
		var instance: StereoSoundInstance? = null
	}

	override fun clientTick(clientLevel: ClientLevel) {
		this.tickCustom(0) { params ->
			val player = localClient.player ?: return@tickCustom
			val (x, _, z) = this.blockPos.center
			val playerX = player.x - x
			val playerZ = player.z - z
			var f2 = this.doMath(Mth.atan2(playerZ, playerX).toFloat() - params.position)
			params.position += f2 * 0.4f
		}

		this.tickCustom(1) { params ->
			var f2 = this.doMath(atan(this.displayFlip))
			params.position = clamp(params.position + (f2 * 0.4f), 0f, 3.15f)
		}
	}

	private fun doMath(input: Float): Float {
		var newInput = input
		while (newInput >= PI.toFloat()) newInput -= (PI * 2).toFloat()
		while (newInput < -PI.toFloat()) newInput += (PI * 2).toFloat()
		return newInput
	}
}
package org.bread_experts_group.breadmod.tool_gun.mode

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.api.ToolGunMode
import org.bread_experts_group.breadmod.registry.sound.ModSounds
import org.bread_experts_group.breadmod.util.entities
import org.bread_experts_group.breadmod.util.rayCast

@ToolGunMode
@Suppress("unused")
class FlingMode : AbstractToolGunMode() {
	override fun action(level: Level, player: Player, stack: ItemStack) {
		val target = player.rayCast(100.0, entities())
		target?.let {
			val entity = it.hit
			level.playSound(null, entity.x, entity.y, entity.z, ModSounds.SCREAM.get(), SoundSource.NEUTRAL, 1f, 1f)
			val vector = player.calculateViewVector(
				player.getViewXRot(0f),
				player.getViewYRot(0f)
			).multiply(80.0, 0.0, 80.0)
			entity.addDeltaMovement(vector.add(0.0, 1.0, 0.0))
		}
	}

	override fun getDisplayName(): Component = Component.literal("Fling")

	override fun getTooltip(): Component = Component.literal("FLING")

	override fun getUid(): ResourceLocation = this.toolGunLocation("fling_mode")
}
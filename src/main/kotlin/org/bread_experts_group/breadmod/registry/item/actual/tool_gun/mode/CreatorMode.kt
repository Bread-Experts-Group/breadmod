package org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.data_holders.common.KeyData

class CreatorMode : AbstractToolGunMode() {
	override fun action(level: Level, player: Player, stack: ItemStack) {
		TODO("Not yet implemented")
	}

	override fun getDisplayName(): Component {
		TODO("Not yet implemented")
	}

	override fun getTooltip(): Component {
		TODO("Not yet implemented")
	}

	override fun getUid(): ResourceLocation = this.toolGunLocation("creator_mode")

	override fun registerKeys(into: MutableMap<Int, KeyData>) {
		super.registerKeys(into)
	}

	override fun saveExtraData(tag: CompoundTag) {
		super.saveExtraData(tag)
	}

	override fun loadExtraData(tag: CompoundTag) {
		super.loadExtraData(tag)
	}
}
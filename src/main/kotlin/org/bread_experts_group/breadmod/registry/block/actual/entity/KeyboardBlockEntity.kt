package org.bread_experts_group.breadmod.registry.block.actual.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup.Provider
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.util.toBlockPos
import org.bread_experts_group.breadmod.util.toIntArray

class KeyboardBlockEntity(
	pos: BlockPos,
	state: BlockState
) : BreadModBlockEntity<KeyboardBlockEntity>(ModBlockEntityTypes.KEYBOARD.get(), pos, state) {
	var monitorPos: BlockPos = BlockPos.ZERO
	override fun saveAdditionalBM(tag: CompoundTag, registries: Provider) {
		tag.putIntArray("monitor", this.monitorPos.toIntArray())
	}

	override fun loadAdditionalBM(tag: CompoundTag, registries: Provider) {
		this.monitorPos = tag.getIntArray("monitor").toBlockPos()
	}

	fun monitorStillValid(): Boolean {
		val level = this.level ?: return false
		return level.getBlockEntity(this.monitorPos) != null
	}

	fun isMonitorBound(): Boolean = this.monitorPos != BlockPos.ZERO
}
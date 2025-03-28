package org.bread_experts_group.breadmod.registry.item.actual.tool_gun

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.network.serverbound.ToolGunDataSyncPacket
import org.bread_experts_group.breadmod.registry.component.ModDataComponents
import org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode.EmptyMode
import org.bread_experts_group.breadmod.util.putValue

data class ToolGunData(
	val mode: IToolGunMode,
	val extraData: CompoundTag,
) {
	var dataLoaded: Boolean = false

	companion object {
		val EMPTY: ToolGunData = ToolGunData(EmptyMode, CompoundTag())
		fun get(stack: ItemStack): ToolGunData = stack.getOrDefault(ModDataComponents.TOOL_GUN_DATA, this.EMPTY)
	}

	fun saveData() {
		this.extraData.put(this.mode.getModeName(), CompoundTag().also(this.mode::saveExtraData))
	}

	fun loadData() {
		val data = this.extraData.getCompound(this.mode.getModeName())
		this.mode.loadExtraData(data)
		this.dataLoaded = true
	}

	inline fun <reified T> modifyValue(key: String, newValue: T) {
		val data = this.extraData.getCompound(this.mode.getModeName())
		if (data.contains(key)) {
			this.extraData.put(this.mode.getModeName(), CompoundTag().also { it.putValue<T>(key, newValue) })
		}
	}

	inline fun <reified T> modifyValueAndSync(key: String, newValue: T) {
		this.modifyValue<T>(key, newValue)
		PacketDistributor.sendToServer(ToolGunDataSyncPacket(this))
	}

	override fun equals(other: Any?): Boolean =
		if (other is ToolGunData) other.mode.getUid() == this.mode.getUid() && this.extraData == other else false

	override fun hashCode(): Int = this.mode.getUid().hashCode()
}
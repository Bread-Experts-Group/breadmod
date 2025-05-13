package org.bread_experts_group.breadmod.data_holders.common

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.network.PacketDistributor
import org.apache.logging.log4j.LogManager
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.network.serverbound.ToolGunDataSyncPacket
import org.bread_experts_group.breadmod.registry.component.ModDataComponents
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.tool_gun.mode.EmptyMode
import org.bread_experts_group.breadmod.util.putValue

typealias KeyData = Pair<Component, (event: InputEvent.Key, stack: ItemStack, player: Player, data: ToolGunData) -> Unit>

data class ToolGunData(
	val mode: IToolGunMode,
	val extraData: CompoundTag,
	val modeIndex: Int
) {
	var dataLoaded: Boolean = false
	val keyData: Map<Int, KeyData> = buildMap {
		val data: MutableMap<Int, KeyData> = mutableMapOf()
		this@ToolGunData.mode.registerKeys(data)
		this.putAll(data)
	}

	companion object {
		val EMPTY: ToolGunData = ToolGunData(EmptyMode, CompoundTag(), 0)
		fun get(stack: ItemStack): ToolGunData {
			check(stack.`is`(ModItems.TOOL_GUN.asItem())) { "Provided ItemStack is not ToolGunItem!" }
			return stack.getOrDefault(ModDataComponents.TOOL_GUN_DATA, this.EMPTY)
		}
	}

	fun syncToServer(): Unit = PacketDistributor.sendToServer(ToolGunDataSyncPacket(this))

	/**
	 * Called when the tool gun is changing modes.
	 */
	fun saveData(level: Level) {
		this.extraData.put(this.mode.getModeName(), CompoundTag().also { this.mode.saveExtraData(it, level) })
	}

	/**
	 * Called when a new mode is loaded or data is updated via sync.
	 */
	fun loadData(level: Level) {
		val data = this.extraData.getCompound(this.mode.getModeName())
		this.mode.loadExtraData(data, level)
		this.dataLoaded = true
	}

	/**
	 * Modifies a value in [extraData], value must already be present in [extraData] to be changed.
	 *
	 * - Automatically syncs to the server.
	 */
	inline fun <reified T> setValue(key: String, newValue: T) {
		val data = this.extraData.getCompound(this.mode.getModeName())
		if (data.contains(key)) {
			data.putValue<T>(key, newValue)
			this.syncToServer()
		} else LogManager.getLogger().warn("$key does not exist, value will not be updated.")
	}

	/**
	 * Directly sets a value in [extraData], regardless if the value exists or not.
	 *
	 * - Automatically syncs to the server.
	 */
	fun setValueDirect(invoker: (CompoundTag) -> Unit) {
		this.extraData.getCompound(this.mode.getModeName()).also(invoker)
		this.syncToServer()
	}

	override fun equals(other: Any?): Boolean =
		if (other is ToolGunData) other.mode.getUid() == this.mode.getUid() && this.extraData == other else false

	override fun hashCode(): Int = this.mode.getUid().hashCode()
}
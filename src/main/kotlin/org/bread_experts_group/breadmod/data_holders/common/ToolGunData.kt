package org.bread_experts_group.breadmod.data_holders.common

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.fml.ModList
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.network.PacketDistributor
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.ModDataComponents
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.api.ToolGunMode
import org.bread_experts_group.breadmod.network.serverbound.ToolGunDataSyncPacket
import org.bread_experts_group.breadmod.registry.Registry
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.tool_gun.mode.EmptyMode
import org.bread_experts_group.breadmod.util.putValue
import org.bread_experts_group.breadmod.util.reflect.LibraryScanner
import kotlin.reflect.full.createInstance

typealias KeyData = Pair<Component, (event: InputEvent.Key, stack: ItemStack, player: Player, data: ToolGunData) -> Unit>

data class ToolGunData(
	val id: ResourceLocation,
	val extraData: CompoundTag,
	val modeIndex: Int
) {
	companion object {
		val logger: Logger = LogManager.getLogger("Tool Gun Data")
		val EMPTY: ToolGunData = ToolGunData(modLocation("tool_gun_empty_mode"), CompoundTag(), 0)
		fun get(stack: ItemStack): ToolGunData {
			check(stack.`is`(ModItems.TOOL_GUN.asItem())) { "Provided ItemStack is not ToolGunItem!" }
			return stack.getOrDefault(ModDataComponents.TOOL_GUN_DATA, this.EMPTY)
		}

		/**
		 * Loads tool gun modes.
		 */
		fun initializeToolGunModes() {
			LibraryScanner.piggyback(data = ModList.get().allScanData).getClassesAnnotatedWith(ToolGunMode::class)
				.forEach {
					val mode = it.createInstance() as IToolGunMode
					if (Registry.toolGunModes[mode.getUid()] == null) {
						Registry.toolGunModes[mode.getUid()] = mode
					} else this.logger.warn("Mode [${mode.getModeName()}] with id ${mode.getUid()} already exists, skipping.")
				}
		}
	}

	var dataLoaded: Boolean = false
	val keyData: Map<Int, KeyData> = buildMap {
		val data: MutableMap<Int, KeyData> = mutableMapOf()
		this@ToolGunData.getMode().registerKeys(data)
		this.putAll(data)
	}

	fun getMode(): IToolGunMode = Registry.toolGunModes.getOrDefault(this.id, EmptyMode)

	fun syncToServer(): Unit = PacketDistributor.sendToServer(ToolGunDataSyncPacket(this))

	/**
	 * Called when the tool gun is changing modes.
	 */
	fun saveData(level: Level) {
		this.extraData.put(this.getMode().getModeName(), CompoundTag().also { this.getMode().saveExtraData(it, level) })
	}

	/**
	 * Called when a new mode is loaded or data is updated via sync.
	 */
	fun loadData(level: Level) {
		val data = this.extraData.getCompound(this.getMode().getModeName())
		this.getMode().loadExtraData(data, level)
		this.dataLoaded = true
	}

	/**
	 * Modifies a value in [extraData], value must already be present in [extraData] to be changed.
	 *
	 * - Automatically syncs to the server.
	 */
	inline fun <reified T> setValue(key: String, newValue: T) {
		val data = this.extraData.getCompound(this.getMode().getModeName())
		if (data.contains(key)) {
			data.putValue<T>(key, newValue)
			this.syncToServer()
		} else Companion.logger.warn("$key does not exist, value will not be updated.")
	}

	/**
	 * Directly sets a value in [extraData], regardless if the value exists or not.
	 *
	 * - Automatically syncs to the server.
	 */
	fun setValueDirect(invoker: (CompoundTag) -> Unit) {
		this.extraData.getCompound(this.getMode().getModeName()).also(invoker)
		this.syncToServer()
	}

	override fun equals(other: Any?): Boolean =
		if (other is ToolGunData) other.getMode().getUid() == this.getMode()
			.getUid() && this.extraData == other else false

	override fun hashCode(): Int = this.getMode().getUid().hashCode()
}
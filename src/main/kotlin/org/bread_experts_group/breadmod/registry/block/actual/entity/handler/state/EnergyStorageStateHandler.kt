package org.bread_experts_group.breadmod.registry.block.actual.entity.handler.state

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.common.util.INBTSerializable
import org.apache.logging.log4j.LogManager
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.handler.ParentedHandler
import org.bread_experts_group.breadmod.util.Color

class EnergyStorageStateHandler : GeneralStateHandler(
	this.COLOR
), INBTSerializable<CompoundTag>, ParentedHandler<BreadModBlockEntity> {
	companion object {
		val COLOR: StateProvisioner<Int> = StateProvisioner(Color.RED)
		val BLOCK_VOID: BlockCapability<EnergyStorageStateHandler, Void?> =
			BlockCapability.createVoid(
				modLocation("energy_storage_state_handler"),
				EnergyStorageStateHandler::class.java
			)
	}

	override lateinit var parent: BreadModBlockEntity

	override fun serializeNBT(provider: HolderLookup.Provider): CompoundTag {
		val tag = CompoundTag()
		tag.putInt("color", this.get(Companion.COLOR))
		LogManager.getLogger().info(tag)
		return tag
	}

	override fun deserializeNBT(
		provider: HolderLookup.Provider,
		nbt: CompoundTag
	) {
		LogManager.getLogger().info(nbt)
		this.set(Companion.COLOR, nbt.getInt("color"))
		this.stateUpdated()
	}
}
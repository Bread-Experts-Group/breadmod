package org.bread_experts_group.breadmod.registry.block.handler.state

import net.minecraft.ChatFormatting
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponentMap
import net.minecraft.nbt.IntTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.common.util.INBTSerializable
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.ModDataComponents
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.handler.DataComponentSerializable
import org.bread_experts_group.breadmod.registry.block.handler.ParentedHandler
import org.bread_experts_group.breadmod.util.Color

class EnergyStorageStateHandler : GeneralStateHandler(
	this.COLOR
), INBTSerializable<Tag>, DataComponentSerializable, ParentedHandler<BreadModBlockEntity> {
	companion object {
		val COLOR: StateProvisioner<Int> = StateProvisioner(Color.RED)
		val BLOCK_VOID: BlockCapability<EnergyStorageStateHandler, Void?> =
			BlockCapability.createVoid(
				modLocation("energy_storage_state_handler"),
				EnergyStorageStateHandler::class.java
			)
	}

	override val stateListeners: MutableList<() -> Unit> = mutableListOf()
	override lateinit var parent: BreadModBlockEntity
	override fun <E> set(provisioner: StateProvisioner<E>, value: E) {
		super.set(provisioner, value)
		this.stateUpdated()
	}

	override fun serializeNBT(provider: HolderLookup.Provider): IntTag = IntTag.valueOf(this.get(COLOR))
	override fun deserializeNBT(
		provider: HolderLookup.Provider,
		nbt: Tag
	) {
		if (nbt !is IntTag) return
		this.set(Companion.COLOR, nbt.asInt)
	}

	override fun serializeDataComponent(map: DataComponentMap.Builder) {
		map.set(ModDataComponents.COLOR, this.get(Companion.COLOR))
	}

	override fun deserializeDataComponent(from: BlockEntity.DataComponentInput) {
		this.set(Companion.COLOR, from.getOrDefault(ModDataComponents.COLOR, Color.RED))
	}

	val colorGray: Component = Component.literal("Color: ").withStyle(ChatFormatting.GRAY)
	val dividerGray: Component = Component.literal("#").withStyle(ChatFormatting.GRAY)
	override fun collectHoverText(tooltipComponents: MutableList<Component>) {
		val color = this.colorGray.copy()
		val colorValue = this.get(Companion.COLOR)
		val hex = colorValue.toUInt().toString(16).padStart(8, '0')
		color.append(this.dividerGray)
		color.append(Component.literal(hex).withColor(colorValue))
		tooltipComponents.add(color)
	}
}
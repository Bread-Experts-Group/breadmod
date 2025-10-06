package org.bread_experts_group.breadmod.registry.block.actual.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup.Provider
import net.minecraft.core.component.DataComponentMap
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.BaseCapability
import net.neoforged.neoforge.common.util.INBTSerializable
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.ModDataComponents.BLOCK_ENTITY_HANDLER_INFORMATION
import org.bread_experts_group.breadmod.network.serverbound.BreadModBEUpdateRequestPacket
import org.bread_experts_group.breadmod.registry.block.actual.BreadModBlock
import org.bread_experts_group.breadmod.registry.block.handler.DataComponentSerializable
import org.bread_experts_group.breadmod.registry.block.handler.HitboxHandler
import org.bread_experts_group.breadmod.registry.block.handler.HitboxHandler.Companion.getHitboxHandler
import org.bread_experts_group.breadmod.registry.block.handler.ParentedHandler
import org.bread_experts_group.breadmod.registry.menu.BreadModMenu
import org.bread_experts_group.breadmod.util.Color.DARK_GRAY
import org.bread_experts_group.breadmod.util.Color.GRAY
import org.bread_experts_group.breadmod.util.Color.LIGHT_GRAY
import org.bread_experts_group.breadmod.util.Color.component

typealias CapabilityMap<T> = Map<BaseCapability<*, *>, Map<Any?, T>>

class BreadModBlockEntity(
	type: BlockEntityType<*>,
	pos: BlockPos,
	state: BlockState,
	capabilityConstructors: CapabilityMap<(BreadModBlockEntity) -> Any> = mapOf(),
) : BlockEntity(type, pos, state), MenuProvider {
	private val capabilities: CapabilityMap<Any> = capabilityConstructors.mapValues { (c, m) ->
		m.mapValues {
			val actual = it.value.invoke(this)
			@Suppress("UNCHECKED_CAST")
			if (actual is ParentedHandler<*>) (actual as ParentedHandler<BlockEntity>).parent = this
			actual
		}
	}

	init {
		for ((_, contextual) in this.capabilities) {
			for ((_, actual) in contextual) {
				if (actual is ParentedHandler<*>) actual.onParentReady()
			}
		}
	}

	override fun getDisplayName(): Component = (this.blockState.block as BreadModBlock).getDisplayName(this)
	override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): BreadModMenu {
		val parent = this.blockState.block as BreadModBlock
		return parent.ofMenu()!!.invoke(parent.menuType!!.get(), containerId, playerInventory, this)
	}

	@Suppress("UNCHECKED_CAST")
	fun <T, C> getCapabilityOrNull(capability: BaseCapability<T, C>, context: C? = null): T? {
		if (this.level?.isClientSide == true)
			PacketDistributor.sendToServer(BreadModBEUpdateRequestPacket(this.blockPos))
		return this.capabilities[capability]?.get(context) as? T
	}

	fun <T, C> getCapability(capability: BaseCapability<T, C>, context: C? = null): T =
		this.getCapabilityOrNull(capability, context)
			?: throw NullPointerException("No capability for [${capability.name()} / $context]")

	override fun saveAdditional(tag: CompoundTag, registries: Provider) {
		super.saveAdditional(tag, registries)
		this.capabilities.forEach { (capability, contextual) ->
			val list = ListTag()
			contextual.forEach { (_, actual) ->
				// TODO: CONTEXT SAVE
				if (actual is INBTSerializable<*>) list.add(actual.serializeNBT(registries))
			}
			if (list.isNotEmpty()) tag.put(capability.name().toString(), list)
		}
	}

	override fun loadAdditional(tag: CompoundTag, registries: Provider) {
		super.loadAdditional(tag, registries)
		for ((capability, contextual) in this.capabilities) {
			val list = tag.get(capability.name().toString()) as? ListTag ?: continue
			for ((i, data) in contextual.entries.iterator().withIndex()) {
				if (i >= list.size) break // Possible corruption
				val (_, actual) = data
				@Suppress("UNCHECKED_CAST")
				if (actual is INBTSerializable<*>)
					(actual as INBTSerializable<Tag>).deserializeNBT(registries, list[i])
			}
		}
	}

	override fun applyImplicitComponents(componentInput: DataComponentInput) {
		for ((_, contextual) in this.capabilities) {
			for ((_, actual) in contextual) {
				if (actual is DataComponentSerializable) actual.deserializeDataComponent(componentInput)
			}
		}
	}

	override fun collectImplicitComponents(components: DataComponentMap.Builder) {
		val counted = mutableMapOf<Any, Triple<BaseCapability<*, *>, MutableList<Any?>, MutableList<Component>>>()
		this.capabilities.forEach { (capability, contextual) ->
			contextual.forEach { (context, actual) ->
				if (actual is DataComponentSerializable) {
					val (_, contextSet, _) = counted.getOrPut(actual) {
						val localText = mutableListOf<Component>()
						actual.serializeDataComponent(components)
						actual.collectHoverText(localText)
						Triple(capability, mutableListOf<Any?>(), localText)
					}
					contextSet.add(context)
				}
			}
		}
		val hoverText = mutableListOf<Component>()
		counted.forEach { (_, data) ->
			val (capability, contextSet, components) = data
			if (components.isEmpty()) return@forEach
			val capabilityHeader = '['.component(DARK_GRAY)
			capabilityHeader.append(LIGHT_GRAY.component(capability.name().toString()))
			hoverText.add(capabilityHeader.append(']'.component(DARK_GRAY)))
			val contextHeader = '['.component(DARK_GRAY)
			contextSet.forEachIndexed { index, item ->
				contextHeader.append(LIGHT_GRAY.component(item.toString()))
				if (index < contextSet.lastIndex) contextHeader.append(','.component(GRAY))
			}
			hoverText.add(contextHeader.append(']'.component(DARK_GRAY)))
			hoverText.addAll(components)
		}
		components.set(BLOCK_ENTITY_HANDLER_INFORMATION, hoverText)
	}

	override fun setRemoved() {
		this.capabilities.forEach { (capability, _) ->
			if (capability == HitboxHandler.BLOCK_VOID) {
				this.getHitboxHandler().discard()
			}
		}
		super.setRemoved()
	}
}
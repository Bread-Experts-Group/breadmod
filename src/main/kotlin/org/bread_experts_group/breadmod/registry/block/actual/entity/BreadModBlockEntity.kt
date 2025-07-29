package org.bread_experts_group.breadmod.registry.block.actual.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup.Provider
import net.minecraft.core.component.DataComponentMap
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.BaseCapability
import net.neoforged.neoforge.common.util.INBTSerializable
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.network.serverbound.BreadModBlockEntityUpdateRequestPacket
import org.bread_experts_group.breadmod.registry.block.actual.entity.handler.DataComponentSerializable
import org.bread_experts_group.breadmod.registry.block.actual.entity.handler.ParentedHandler
import java.util.Optional

typealias CapabilityMap = Map<BaseCapability<*, *>, Map<Optional<out Any>, (BreadModBlockEntity, Any?) -> Any>>
typealias MutableCapabilityMap<T> = MutableMap<BaseCapability<*, *>, MutableMap<Optional<out Any>, T>>

class BreadModBlockEntity(
	type: BlockEntityType<*>,
	pos: BlockPos,
	state: BlockState,
	private val capabilityConstructors: CapabilityMap = mapOf(),
) : BlockEntity(type, pos, state) {
	private val loadedCapabilities: MutableSet<Any> = mutableSetOf()
	private val capabilities: MutableCapabilityMap<Any> = mutableMapOf()
	private val prepLoad: MutableCapabilityMap<Pair<Provider, Tag>> = mutableMapOf()
	private var prepInput: DataComponentInput? = null

	@Suppress("UNCHECKED_CAST")
	fun <T, C> getCapability(capability: BaseCapability<T, C>, context: C? = null): T {
		val actual = this.capabilities
			.getOrPut(capability) { mutableMapOf() }
			.getOrPut(Optional.ofNullable(context)) {
				if (this.level?.isClientSide == true)
					PacketDistributor.sendToServer(BreadModBlockEntityUpdateRequestPacket(this.blockPos))
				val contextual = this.capabilityConstructors[capability]
					?: throw NullPointerException("No capability for [${capability.name()}]")
				val nullableContext = Optional.ofNullable(context)
				val actual = contextual[nullableContext]?.invoke(this, context) as? T
					?: throw NullPointerException("No contextual actual for [${capability.name()} / $context]")
				if (this.loadedCapabilities.add(actual)) {
					if (actual is ParentedHandler<*>) (actual as ParentedHandler<BlockEntity>).parent = this
					if (actual is INBTSerializable<*>) this.prepLoad[capability]?.let {
						val contextPrep = it[nullableContext] ?: return@let
						(actual as INBTSerializable<Any>).deserializeNBT(contextPrep.first, contextPrep.second)
						it.remove(nullableContext)
						if (it.isEmpty()) this.prepLoad.remove(capability)
					}
					this.prepInput?.let {
						if (actual is DataComponentSerializable) actual.deserializeDataComponent(it)
					}
				}
				actual
			} as T
		return actual
	}

	override fun saveAdditional(tag: CompoundTag, registries: Provider) {
		super.saveAdditional(tag, registries)
		this.prepLoad.forEach { (capability, contextual) ->
			val list = ListTag()
			contextual.forEach { (_, actual) -> list.add(actual.second) }
			if (list.isNotEmpty()) tag.put(capability.name().toLanguageKey(), list)
		}
		this.capabilities.forEach { (capability, contextual) ->
			val list = ListTag()
			contextual.forEach { (_, actual) ->
				// TODO: CONTEXT SAVE
				if (actual is INBTSerializable<*>) list.add(actual.serializeNBT(registries))
			}
			if (list.isNotEmpty()) tag.put(capability.name().toLanguageKey(), list)
		}
	}

	override fun loadAdditional(tag: CompoundTag, registries: Provider) {
		super.loadAdditional(tag, registries)
		this.prepLoad.clear()
		for ((capability, contextual) in this.capabilityConstructors) {
			val list = tag.get(capability.name().toLanguageKey()) as? ListTag ?: continue
			val loadMap = this.capabilities[capability]
			val prepMap = this.prepLoad.getOrPut(capability) { mutableMapOf() }
			for ((i, data) in contextual.entries.iterator().withIndex()) {
				if (i >= list.size) break // Possible corruption
				val (context, _) = data
				// TODO: CONTEXT SAVE
				val loadCheck = loadMap?.get(context)
				@Suppress("UNCHECKED_CAST")
				if (loadCheck != null && loadCheck is INBTSerializable<*>) (loadCheck as INBTSerializable<Tag>).deserializeNBT(
					registries, list[i]
				) else prepMap[context] = registries to list[i]
			}
		}
	}

	override fun applyImplicitComponents(componentInput: DataComponentInput) {
		this.capabilities.forEach { (_, contextual) ->
			contextual.forEach { (_, actual) ->
				if (actual is DataComponentSerializable) actual.deserializeDataComponent(componentInput)
			}
		}
		this.prepInput = componentInput
	}

	override fun collectImplicitComponents(components: DataComponentMap.Builder) {
		this.capabilities.forEach { (_, contextual) ->
			contextual.forEach { (_, actual) ->
				if (actual is DataComponentSerializable) actual.serializeDataComponent(components)
			}
		}
	}
}
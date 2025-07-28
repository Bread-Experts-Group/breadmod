package org.bread_experts_group.breadmod.registry.block.actual.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup.Provider
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.BaseCapability
import net.neoforged.neoforge.common.util.INBTSerializable
import org.bread_experts_group.breadmod.registry.block.actual.entity.handler.ParentedHandler
import java.util.Optional

class BreadModBlockEntity(
	type: BlockEntityType<*>,
	pos: BlockPos,
	state: BlockState,
	private val capabilities: Map<BaseCapability<*, *>, Map<Optional<Any>, Any>> = mapOf(),
) : BlockEntity(type, pos, state) {
	@Suppress("UNCHECKED_CAST")
	fun <T, C> getCapability(capability: BaseCapability<T, C>, context: C? = null): T {
		val contextual = this.capabilities[capability]
			?: throw NullPointerException("No capability for [${capability.name()}]")
		val actual = contextual[Optional.ofNullable(context)] as? T
			?: throw NullPointerException("No contextual actual for [${capability.name()} / $context]")
		if (actual is ParentedHandler<*>) (actual as ParentedHandler<BlockEntity>).parent = this
		return actual
	}

	fun applyCapabilitySubscriptions(on: MutableMap<BaseCapability<*, *>, (BreadModBlockEntity, Any?) -> Any>) {
		for ((capability, _) in this.capabilities) on[capability] = { _, c ->
			@Suppress("UNCHECKED_CAST")
			this.getCapability(
				capability as BaseCapability<*, Any>,
				c
			)
		}
	}

	override fun saveAdditional(tag: CompoundTag, registries: Provider) {
		super.saveAdditional(tag, registries)
		this.capabilities.forEach { (capability, contextual) ->
			val list = ListTag()
			contextual.forEach { (_, actual) ->
				// TODO: CONTEXT SAVE
				if (actual is INBTSerializable<*>) list.add(actual.serializeNBT(registries))
			}
			tag.put(capability.name().toLanguageKey(), list)
		}
	}

	override fun loadAdditional(tag: CompoundTag, registries: Provider) {
		super.loadAdditional(tag, registries)
		for ((capability, contextual) in this.capabilities) {
			val list = tag.get(capability.name().toLanguageKey()) as? ListTag ?: continue
			var i = 0
			contextual.forEach { (_, actual) ->
				// TODO: CONTEXT SAVE
				@Suppress("UNCHECKED_CAST")
				if (actual is ParentedHandler<*>) (actual as ParentedHandler<BlockEntity>).parent = this
				@Suppress("UNCHECKED_CAST")
				if (actual is INBTSerializable<*>) (actual as INBTSerializable<Tag>).deserializeNBT(
					registries, list[i]
				)
				i++
			}
		}
	}

	override fun getUpdatePacket(): Packet<ClientGamePacketListener> = ClientboundBlockEntityDataPacket.create(this)
	override fun getUpdateTag(registries: Provider): CompoundTag = CompoundTag().also {
		this.saveAdditional(it, registries)
	}
}
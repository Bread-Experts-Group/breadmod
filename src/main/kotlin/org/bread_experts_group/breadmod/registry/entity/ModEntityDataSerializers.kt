package org.bread_experts_group.breadmod.registry.entity

import net.minecraft.core.BlockPos
import net.minecraft.network.syncher.EntityDataSerializer
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import org.bread_experts_group.breadmod.network.BreadModCodecs
import org.bread_experts_group.breadmod.network.BreadModCodecs.BLOCK_MAP_STREAM_CODEC
import org.bread_experts_group.breadmod.registry.RegistryProvider
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.BigDescriptor
import org.bread_experts_group.breadmod.util.listOf

@Suppress("unused")
object ModEntityDataSerializers : RegistryProvider(
	NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS
) {
	private val registry: DeferredRegister<EntityDataSerializer<*>> = this.getRegistry(
		NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS
	)
	val BLOCK_MAP: DeferredHolder<EntityDataSerializer<*>, EntityDataSerializer<Map<BlockPos, BlockState>>> =
		this.registry.register("block_map") { ->
			EntityDataSerializer.forValueType(BLOCK_MAP_STREAM_CODEC)
		}
	val BIG_DESCRIPTOR_ITEM: DeferredHolder<
			EntityDataSerializer<*>,
			EntityDataSerializer<List<BigDescriptor<Item>>>
			> =
		this.registry.register("big_descriptor_item") { ->
			EntityDataSerializer.forValueType(BreadModCodecs.BIG_DESCRIPTOR_ITEM_STREAM_CODEC.listOf())
		}
}
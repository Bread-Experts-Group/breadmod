package org.bread_experts_group.breadmod.registry.block.actual.entity

import net.minecraft.core.Direction
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.capabilities.ItemCapability

@Suppress("unused")
interface FacingSensitiveProviderRetriever {
	fun <T, C> getIndexForBlockProvider(provider: BlockCapability<T, C>, side: Direction): Int {
		throw NotImplementedError("This method should be overridden by the implementing class.")
	}

	fun <T, C> getIndexForItemProvider(provider: ItemCapability<T, C>, side: Direction): Int {
		throw NotImplementedError("This method should be overridden by the implementing class.")
	}
}
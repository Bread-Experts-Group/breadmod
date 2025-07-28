package org.bread_experts_group.breadmod.registry

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.BreadMod

typealias RegistryKey<V> = ResourceKey<out Registry<V>>

abstract class RegistryProvider(vararg registers: RegistryKey<out Any>) : Iterable<DeferredRegister<out Any>> {
	private val registries: Map<RegistryKey<out Any>, DeferredRegister<out Any>> = registers.associateWith {
		@Suppress("UNCHECKED_CAST")
		DeferredRegister.create(it as ResourceKey<out Registry<Any>>, BreadMod.ID)
	}

	override fun toString(): String = "RegistryProvider[${this.registries.values}]"
	final override fun iterator(): Iterator<DeferredRegister<out Any>> = this.registries.values.iterator()

	@Suppress("UNCHECKED_CAST")
	fun <T> getRegistry(key: RegistryKey<out T>): DeferredRegister<T> =
		this.registries.getValue(key) as DeferredRegister<T>
}
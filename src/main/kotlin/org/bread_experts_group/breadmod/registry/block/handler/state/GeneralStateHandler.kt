package org.bread_experts_group.breadmod.registry.block.handler.state

abstract class GeneralStateHandler(vararg provisioners: StateProvisioner<out Any>) {
	private val state: MutableMap<StateProvisioner<*>, Any?> = provisioners.associateWith<StateProvisioner<*>, Any> {
		it.default as Any
	}.toMutableMap()

	fun <E : Any?> getOrNull(provisioner: StateProvisioner<E>): E? {
		@Suppress("UNCHECKED_CAST")
		return this.state[provisioner] as? E
	}

	fun <E : Any> get(provisioner: StateProvisioner<E>): E {
		@Suppress("UNCHECKED_CAST")
		return this.state.getValue(provisioner) as E
	}

	open fun <E : Any?> set(provisioner: StateProvisioner<E>, value: E) {
		this.state[provisioner] = value
	}

	fun invert(provisioner: StateProvisioner<Boolean>): Boolean {
		val set = !this.get(provisioner)
		this.set(provisioner, set)
		return set
	}

	class StateProvisioner<T>(val default: T)
}
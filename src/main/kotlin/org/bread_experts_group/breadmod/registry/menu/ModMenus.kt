package org.bread_experts_group.breadmod.registry.menu

import net.minecraft.core.registries.Registries
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension
import net.neoforged.neoforge.network.IContainerFactory
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.registry.RegistryProvider
import java.util.function.Supplier

object ModMenus : RegistryProvider(Registries.MENU) {
	private val registry: DeferredRegister<MenuType<*>> = this.getRegistry(Registries.MENU)
//	val WHEAT_CRUSHER: Supplier<MenuType<WheatCrusherMenu>> =
//		this.registerMenuType("wheat_crusher_menu", ::WheatCrusherMenu)
//	val DOUGH_MACHINE: Supplier<MenuType<DoughMachineMenu>> =
//		this.registerMenuType("dough_machine_menu", ::DoughMachineMenu)
	// Exp.
//	val FLUID_ENERGY_TEST: Supplier<MenuType<FluidEnergyMenu>> =
//		this.registerMenuType("fluid_energy", ::FluidEnergyMenu)
	private fun <T : AbstractContainerMenu> registerMenuType(
		name: String,
		menu: IContainerFactory<T>
	): Supplier<MenuType<T>> = this.registry.register(name) { -> IMenuTypeExtension.create(menu) }
}
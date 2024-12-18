package org.bread_experts_group.breadmod.registry.menu

import net.minecraft.core.registries.Registries
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension
import net.neoforged.neoforge.network.IContainerFactory
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.experimental.fluid_energy_recipe.FluidEnergyMenu
import org.bread_experts_group.breadmod.experimental.recipe_related.block.multi_fluid.MultiFluidRecipeMenu
import org.bread_experts_group.breadmod.experimental.recipe_related.block.multi_item.MultiItemRecipeMenu
import org.bread_experts_group.breadmod.experimental.recipe_related.block.single_fluid.SingleFluidRecipeMenu
import org.bread_experts_group.breadmod.experimental.recipe_related.block.single_fluid_item.SingleFluidItemRecipeMenu
import org.bread_experts_group.breadmod.experimental.recipe_related.block.single_item.SingleItemRecipeMenu
import org.bread_experts_group.breadmod.registry.menu.actual.DoughMachineMenu
import org.bread_experts_group.breadmod.registry.menu.actual.WheatCrusherMenu
import java.util.function.Supplier

object ModMenuTypes {
    val MENU_TYPE_REGISTRY: DeferredRegister<MenuType<*>> = DeferredRegister.create(Registries.MENU, BreadMod.ID)

    val WHEAT_CRUSHER: Supplier<MenuType<WheatCrusherMenu>> = registerMenuType("wheat_crusher_menu", ::WheatCrusherMenu)
    val DOUGH_MACHINE: Supplier<MenuType<DoughMachineMenu>> = registerMenuType("dough_machine_menu", ::DoughMachineMenu)

    // Exp.

    val MULTI_ITEM: Supplier<MenuType<MultiItemRecipeMenu>> = registerMenuType("multi_item", ::MultiItemRecipeMenu)
    val MULTI_FLUID: Supplier<MenuType<MultiFluidRecipeMenu>> = registerMenuType("multi_fluid", ::MultiFluidRecipeMenu)
    val SINGLE_ITEM: Supplier<MenuType<SingleItemRecipeMenu>> = registerMenuType("single_item", ::SingleItemRecipeMenu)
    val SINGLE_FLUID: Supplier<MenuType<SingleFluidRecipeMenu>> =
        registerMenuType("single_fluid", ::SingleFluidRecipeMenu)
    val SINGLE_FLUID_ITEM: Supplier<MenuType<SingleFluidItemRecipeMenu>> =
        registerMenuType("single_fluid_item", ::SingleFluidItemRecipeMenu)

    val FLUID_ENERGY_TEST: Supplier<MenuType<FluidEnergyMenu>> =
        registerMenuType("fluid_energy", ::FluidEnergyMenu)

    private fun <T : AbstractContainerMenu> registerMenuType(
        name: String,
        menu: IContainerFactory<T>
    ): Supplier<MenuType<T>> =
        MENU_TYPE_REGISTRY.register(name) { ->
            IMenuTypeExtension.create(menu)
        }
}
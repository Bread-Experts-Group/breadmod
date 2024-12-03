package org.bread_experts_group.breadmod.registry.menu

import net.minecraft.core.registries.Registries
import net.minecraft.world.inventory.MenuType
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.experimental.block.multi_fluid.MultiFluidRecipeMenu
import org.bread_experts_group.breadmod.experimental.block.multi_item.MultiItemRecipeMenu
import org.bread_experts_group.breadmod.experimental.block.single_fluid.SingleFluidRecipeMenu
import org.bread_experts_group.breadmod.experimental.block.single_item.SingleItemRecipeMenu
import org.bread_experts_group.breadmod.registry.menu.actual.DoughMachineMenu
import org.bread_experts_group.breadmod.registry.menu.actual.WheatCrusherMenu
import java.util.function.Supplier

object ModMenuTypes {
    val MENU_TYPE_REGISTRY: DeferredRegister<MenuType<*>> = DeferredRegister.create(Registries.MENU, BreadMod.ID)

    val WHEAT_CRUSHER: Supplier<MenuType<WheatCrusherMenu>> = MENU_TYPE_REGISTRY.register("wheat_crusher_menu") { ->
        IMenuTypeExtension.create(::WheatCrusherMenu)
    }

    val DOUGH_MACHINE: Supplier<MenuType<DoughMachineMenu>> = MENU_TYPE_REGISTRY.register("dough_machine_menu") { ->
        IMenuTypeExtension.create(::DoughMachineMenu)
    }

    // Exp.

    val MULTI_ITEM: Supplier<MenuType<MultiItemRecipeMenu>> = MENU_TYPE_REGISTRY.register("multi_item") { ->
        IMenuTypeExtension.create(::MultiItemRecipeMenu)
    }

    val MULTI_FLUID: Supplier<MenuType<MultiFluidRecipeMenu>> = MENU_TYPE_REGISTRY.register("multi_fluid") { ->
        IMenuTypeExtension.create(::MultiFluidRecipeMenu)
    }

    val SINGLE_ITEM: Supplier<MenuType<SingleItemRecipeMenu>> = MENU_TYPE_REGISTRY.register("single_item") { ->
        IMenuTypeExtension.create(::SingleItemRecipeMenu)
    }

    val SINGLE_FLUID: Supplier<MenuType<SingleFluidRecipeMenu>> = MENU_TYPE_REGISTRY.register("single_fluid") { ->
        IMenuTypeExtension.create(::SingleFluidRecipeMenu)
    }
}
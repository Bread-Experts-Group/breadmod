package org.bread_experts_group.breadmod.registry.menu

import net.minecraft.core.registries.Registries
import net.minecraft.world.inventory.MenuType
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.Breadmod
import org.bread_experts_group.breadmod.menu.WheatCrusherMenu
import java.util.function.Supplier

object ModMenuTypes {
    val MENU_TYPE_REGISTRY: DeferredRegister<MenuType<*>> = DeferredRegister.create(Registries.MENU, Breadmod.ID)

    val WHEAT_CRUSHER: Supplier<MenuType<WheatCrusherMenu>> = MENU_TYPE_REGISTRY.register("wheat_crusher_menu") { ->
        IMenuTypeExtension.create(::WheatCrusherMenu)
    }
}
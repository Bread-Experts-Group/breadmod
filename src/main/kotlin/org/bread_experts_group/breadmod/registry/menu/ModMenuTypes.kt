package org.bread_experts_group.breadmod.registry.menu

import net.minecraft.core.registries.Registries
import net.minecraft.world.inventory.MenuType
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.registry.menu.actual.DoughMachineMenu
import org.bread_experts_group.breadmod.registry.menu.actual.WheatCrusherMenu
import org.bread_experts_group.breadmod.registry.menu.actual.item.CertificateMenu
import java.util.function.Supplier

object ModMenuTypes {
    val MENU_TYPE_REGISTRY: DeferredRegister<MenuType<*>> = DeferredRegister.create(Registries.MENU, BreadMod.ID)

    val WHEAT_CRUSHER: Supplier<MenuType<WheatCrusherMenu>> = MENU_TYPE_REGISTRY.register("wheat_crusher_menu") { ->
        IMenuTypeExtension.create(::WheatCrusherMenu)
    }

    val DOUGH_MACHINE: Supplier<MenuType<DoughMachineMenu>> = MENU_TYPE_REGISTRY.register("dough_machine_menu") { ->
        IMenuTypeExtension.create(::DoughMachineMenu)
    }

    val CERTIFICATE: Supplier<MenuType<CertificateMenu>> = MENU_TYPE_REGISTRY.register("certificate_menu") { ->
        IMenuTypeExtension.create { windowId, inv, _ -> CertificateMenu(windowId, inv) }
    }
}
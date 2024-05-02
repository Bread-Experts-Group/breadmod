package breadmod.registry.screen

import breadmod.BreadMod
import breadmod.block.entity.menu.DoughMachineMenu
import net.minecraft.world.inventory.MenuType
import net.minecraftforge.common.extensions.IForgeMenuType
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModMenuTypes {
    val deferredRegister: DeferredRegister<MenuType<*>> = DeferredRegister.create(ForgeRegistries.MENU_TYPES, BreadMod.ID)

    val DOUGH_MACHINE: RegistryObject<MenuType<DoughMachineMenu>> = deferredRegister.register("dough_machine_menu") {
        IForgeMenuType.create { pContainerId, pInventory, extraData -> DoughMachineMenu(pContainerId, pInventory, extraData) }
    }
}
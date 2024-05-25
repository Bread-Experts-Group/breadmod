package breadmod.registry.screen

import breadmod.ModMain
import breadmod.block.entity.menu.DoughMachineMenu
import breadmod.block.entity.menu.WheatCrusherMenu
import net.minecraft.world.inventory.MenuType
import net.minecraftforge.common.extensions.IForgeMenuType
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModMenuTypes {
    val deferredRegister: DeferredRegister<MenuType<*>> = DeferredRegister.create(ForgeRegistries.MENU_TYPES, ModMain.ID)

    val DOUGH_MACHINE: RegistryObject<MenuType<DoughMachineMenu>> = deferredRegister.register("dough_machine_menu") {
        IForgeMenuType.create { pContainerId, pInventory, extraData -> DoughMachineMenu(pContainerId, pInventory, extraData) }
    }
    val WHEAT_CRUSHER: RegistryObject<MenuType<WheatCrusherMenu>> = deferredRegister.register("wheat_crusher_menu") {
        IForgeMenuType.create { pContainerId, pInventory, extraData -> WheatCrusherMenu(pContainerId, pInventory, extraData)}
    }
}
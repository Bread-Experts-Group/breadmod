package breadmod.registry.screen

import breadmod.BreadMod
import breadmod.block.entity.menu.BreadFurnaceMenu
import breadmod.block.entity.menu.DoughMachineMenu
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.inventory.MenuType
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModMenuTypes {
    val deferredRegister: DeferredRegister<MenuType<*>> = DeferredRegister.create(ForgeRegistries.MENU_TYPES, BreadMod.ID)

    val BREAD_FURNACE: RegistryObject<MenuType<BreadFurnaceMenu>> = deferredRegister.register("bread_furnace") {
        MenuType({ pContainerId, pInventory -> BreadFurnaceMenu(pContainerId, pInventory) }, FeatureFlags.VANILLA_SET)
    }

//    val DOUGH_MACHINE: RegistryObject<MenuType<DoughMachineMenu>> = deferredRegister.register("dough_furnace") {
//        MenuType({ pMenuType, pInventory -> DoughMachineMenu(pMenuType, pInventory) }, FeatureFlags.VANILLA_SET)
//    } //TODO Stop it from erroring out
}
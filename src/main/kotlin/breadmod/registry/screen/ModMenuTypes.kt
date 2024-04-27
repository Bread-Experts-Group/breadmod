package breadmod.registry.screen

import breadmod.BreadMod
import breadmod.block.entity.menu.BreadFurnaceMenu
import breadmod.block.entity.menu.DoughMachineMenu
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraftforge.common.extensions.IForgeMenuType
import net.minecraftforge.network.IContainerFactory
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModMenuTypes {
    val deferredRegister: DeferredRegister<MenuType<*>> = DeferredRegister.create(ForgeRegistries.MENU_TYPES, BreadMod.ID)

    val BREAD_FURNACE: RegistryObject<MenuType<BreadFurnaceMenu>> = deferredRegister.register("bread_furnace") {
        MenuType({ pContainerId, pInventory -> BreadFurnaceMenu(pContainerId, pInventory) }, FeatureFlags.VANILLA_SET)
    }

    val DOUGH_MACHINE: RegistryObject<MenuType<DoughMachineMenu>> =
        registerMenuType("dough_machine_menu") { pContainerId : Int, pInventory : Inventory, extraData : FriendlyByteBuf ->
            DoughMachineMenu(pContainerId,pInventory, extraData)
        }

    private fun <T : AbstractContainerMenu?> registerMenuType(
        name: String,
        factory: IContainerFactory<T>
    ): RegistryObject<MenuType<T>> {
        return deferredRegister.register(name) { IForgeMenuType.create(factory) }
    }
}
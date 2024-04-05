package breadmod.screens

import breadmod.BreadMod
import breadmod.block.BreadFurnaceBlock
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.inventory.MenuType
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModMenuTypes {
    val REGISTRY: DeferredRegister<MenuType<*>> = DeferredRegister.create(ForgeRegistries.MENU_TYPES, BreadMod.ID)

    val BREAD_FURNACE: RegistryObject<MenuType<BreadFurnaceBlock.Menu>> = REGISTRY.register("bread_furnace") {
        MenuType({ pContainerId, pInventory ->
            BreadFurnaceBlock.Menu(pContainerId, pInventory)
        }, FeatureFlags.VANILLA_SET)
    }
}
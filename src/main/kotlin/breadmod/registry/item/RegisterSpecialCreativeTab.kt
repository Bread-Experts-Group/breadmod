package breadmod.registry.item

import net.minecraft.world.item.CreativeModeTab
import net.minecraftforge.registries.RegistryObject

interface RegisterSpecialCreativeTab {
    val creativeModeTab: RegistryObject<CreativeModeTab>
    fun displayInCreativeTab(pParameters: CreativeModeTab.ItemDisplayParameters, pOutput: CreativeModeTab.Output)
}
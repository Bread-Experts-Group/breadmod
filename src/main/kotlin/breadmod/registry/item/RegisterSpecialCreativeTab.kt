package breadmod.registry.item

import net.minecraft.world.item.CreativeModeTab

interface RegisterSpecialCreativeTab {
    fun displayInCreativeTab(pParameters: CreativeModeTab.ItemDisplayParameters, pOutput: CreativeModeTab.Output)
}
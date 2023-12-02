package breadmod.gui

import breadmod.BreadMod
import breadmod.item.ModItems
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack

object BreadModCreativeTab: CreativeModeTab(BreadMod.ID) {
    override fun makeIcon(): ItemStack = ModItems.BREAD_BLOCK_ITEM.defaultInstance
    override fun hasSearchBar(): Boolean = true

    override fun getBackgroundImage(): ResourceLocation {
        return super.getBackgroundImage()
        // BACKGROUND IMAGES FOR CREATIVE TABS
        // quickly! commission yeta
    }
}
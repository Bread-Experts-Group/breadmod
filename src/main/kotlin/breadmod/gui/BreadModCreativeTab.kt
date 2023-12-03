package breadmod.gui

import breadmod.BreadMod
import breadmod.item.ModItems
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack

object BreadModCreativeTab: CreativeModeTab(BreadMod.ID) {
    override fun makeIcon(): ItemStack = ModItems.BREAD_BLOCK_ITEM.defaultInstance
    override fun hasSearchBar(): Boolean = true

    /*
    override fun setBackgroundImage(texture: ResourceLocation): CreativeModeTab {
        return super.setBackgroundImage(texture)
    }
    */

    override fun getBackgroundImage(): ResourceLocation {
        return super.getBackgroundImage()
        // BACKGROUND IMAGES FOR CREATIVE TABS
        // quickly! commission yeta
    }
}
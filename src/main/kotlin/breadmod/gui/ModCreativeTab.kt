package breadmod.gui

import breadmod.BreadMod
import breadmod.item.ModItems
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack

object ModCreativeTab: CreativeModeTab(BreadMod.ID) { override fun makeIcon(): ItemStack = ModItems.BREAD_BLOCK_ITEM.defaultInstance }
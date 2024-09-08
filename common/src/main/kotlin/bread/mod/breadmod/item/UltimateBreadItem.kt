package bread.mod.breadmod.item

import bread.mod.breadmod.registry.item.IRegisterSpecialCreativeTab
import bread.mod.breadmod.registry.menu.ModCreativeTabs
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties
import net.minecraft.world.item.ItemStack

class UltimateBreadItem : Item(Properties().stacksTo(1).fireResistant()), IRegisterSpecialCreativeTab {

//    fun setTimeLeft(stack: ItemStack, ticks: Long) = stack.orCreateTag.putLong(TIME_LEFT_NBT, ticks)

    override val creativeModeTabs: List<RegistrySupplier<CreativeModeTab>> = listOf(ModCreativeTabs.SPECIALS_TAB)
    override fun displayInCreativeTab(
        pParameters: CreativeModeTab.ItemDisplayParameters,
        pOutput: CreativeModeTab.Output,
    ): Boolean {
        pOutput.accept(ItemStack(this))/*.also {
            this.setTimeLeft(
                it,
                1000 // todo replace with config when it's available
            )
        })*/
//        pOutput.accept(ItemStack(this).also { this.setTimeLeft(it, Long.MAX_VALUE) })
        return false
    }

/*    override fun getBarWidth(pStack: ItemStack): Int =
        (13F * (getTimeLeft(pStack) / MAX_TIME_CONFIG.get())).roundToInt()*/

    // todo port this over
/*    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        super.inventoryTick(stack, level, entity, slotId, isSelected)
    }*/

    // todo doesn't exist in common
//    override fun getEntityLifespan(itemStack: ItemStack, level: Level): Int = 0

    companion object {
//        val MAX_TIME_CONFIG = ModConfiguration.COMMON.ULTIMATE_BREAD_MAX_CREATIVE_TIME_TICKS
        const val TIME_LEFT_NBT = "ticksLeft"
    }
}
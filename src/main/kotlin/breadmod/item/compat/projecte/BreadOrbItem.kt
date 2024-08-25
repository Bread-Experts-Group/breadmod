package breadmod.item.compat.projecte

import breadmod.ModMain.modTranslatable
import breadmod.registry.item.IRegisterSpecialCreativeTab
import breadmod.registry.menu.ModCreativeTabs
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder
import moze_intel.projecte.capability.EmcHolderItemCapabilityWrapper
import moze_intel.projecte.gameObjs.items.IBarHelper
import moze_intel.projecte.gameObjs.items.ItemPE
import moze_intel.projecte.integration.IntegrationHelper
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraftforge.fml.ModList
import net.minecraftforge.registries.RegistryObject
import kotlin.math.min

class BreadOrbItem : ItemPE(Properties().stacksTo(1).rarity(Rarity.RARE)), IItemEmcHolder, IBarHelper,
    IRegisterSpecialCreativeTab {
    private val emcAmount = 45000

    init {
        this.addItemCapability { EmcHolderItemCapabilityWrapper() }
        if (ModList.get().isLoaded("curios")) {
            this.addItemCapability("curios", IntegrationHelper.CURIO_CAP_SUPPLIER)
            // Just hook into ProjectE's curios implementation since this item needs ProjectE to exist
        }
    }

    override fun isBarVisible(pStack: ItemStack): Boolean = true
    override fun getWidthForBar(pStack: ItemStack): Float {
        val itemEmc: Long = getEmc(pStack)
        // Divides currently stored EMC by max EMC and subtracts by 1
        return if (itemEmc == 0L) 1.0f else (1.0 - itemEmc.toDouble() / emcAmount).toFloat()
    }

    override fun getBarWidth(pStack: ItemStack): Int = getScaledBarWidth(pStack)

    override fun insertEmc(pStack: ItemStack, pLong: Long, pEmcAction: IEmcStorage.EmcAction?): Long {
        if (pLong < 0L) return this.extractEmc(pStack, -pLong, pEmcAction) else {
            val toAdd = min(getNeededEmc(pStack).toDouble(), pLong.toDouble()).toLong()
            if (pEmcAction != null) {
                if (pEmcAction.execute()) addEmcToStack(pStack, toAdd)
            }
            return toAdd
        }
    }

    override fun extractEmc(pStack: ItemStack, pLong: Long, pEmcAction: IEmcStorage.EmcAction?): Long {
        if (pLong < 0L) return this.insertEmc(pStack, -pLong, pEmcAction) else {
            val storedEmc = this.getStoredEmc(pStack)
            val toRemove = min(storedEmc.toDouble(), pLong.toDouble()).toLong()
            if (pEmcAction != null) {
                if (pEmcAction.execute()) setEmc(pStack, storedEmc - toRemove)
            }
            return toRemove
        }
    }

    override fun appendHoverText(
        pStack: ItemStack,
        pLevel: Level?,
        pTooltipComponents: MutableList<Component>,
        pIsAdvanced: TooltipFlag
    ) {
        pTooltipComponents.add(modTranslatable("item", "bread_orb", "tooltip").withStyle(ChatFormatting.GOLD))
    }

    override fun getStoredEmc(pStack: ItemStack): Long = getEmc(pStack)
    override fun getMaximumEmc(pStack: ItemStack): Long = emcAmount.toLong()

    override val creativeModeTabs: List<RegistryObject<CreativeModeTab>> = listOf(ModCreativeTabs.MAIN_TAB)
    override fun displayInCreativeTab(
        pParameters: CreativeModeTab.ItemDisplayParameters,
        pOutput: CreativeModeTab.Output
    ): Boolean {
        pOutput.accept(ItemStack(this).also { this.insertEmc(it, 45000, IEmcStorage.EmcAction.EXECUTE) })
        return true
    }
}
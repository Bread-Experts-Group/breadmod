package breadmod.item

import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder
import moze_intel.projecte.capability.EmcHolderItemCapabilityWrapper
import moze_intel.projecte.gameObjs.items.ItemPE
import net.minecraft.world.item.ItemStack
import kotlin.math.min

class BreadEMCItem : ItemPE(Properties()), IItemEmcHolder {

    init {
        this.addItemCapability { EmcHolderItemCapabilityWrapper() }
    }

    override fun insertEmc(pStack: ItemStack, pLong: Long, pEmcAction: IEmcStorage.EmcAction?): Long {
        if (pLong < 0L) {
            return this.extractEmc(pStack, -pLong, pEmcAction)
        } else {
            val toAdd = min(getNeededEmc(pStack).toDouble(), pLong.toDouble()).toLong()
            if (pEmcAction != null) {
                if (pEmcAction.execute()) {
                    addEmcToStack(pStack, toAdd)
                }
            }

            return toAdd
        }
    }

    override fun extractEmc(pStack: ItemStack, pLong: Long, pEmcAction: IEmcStorage.EmcAction?): Long {
        if (pLong < 0L) {
            return this.insertEmc(pStack, -pLong, pEmcAction)
        } else {
            val storedEmc = this.getStoredEmc(pStack)
            val toRemove = min(storedEmc.toDouble(), pLong.toDouble()).toLong()
            if (pEmcAction != null) {
                if (pEmcAction.execute()) {
                    setEmc(pStack, storedEmc - toRemove)
                }
            }

            return toRemove
        }
    }

    override fun getStoredEmc(pStack: ItemStack): Long = getEmc(pStack)

    override fun getMaximumEmc(pStack: ItemStack): Long = 100000

}
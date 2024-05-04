package breadmod.item

import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

class BreadEMCItem : Item(Properties()), IItemEmcHolder {
    override fun insertEmc(p0: ItemStack, p1: Long, p2: IEmcStorage.EmcAction?): Long {
        TODO("Not yet implemented")
    }

    override fun extractEmc(p0: ItemStack, p1: Long, p2: IEmcStorage.EmcAction?): Long {
        TODO("Not yet implemented")
    }

    override fun getStoredEmc(p0: ItemStack): Long {
        TODO("Not yet implemented")
    }

    override fun getMaximumEmc(p0: ItemStack): Long {
        TODO("Not yet implemented")
    }
}
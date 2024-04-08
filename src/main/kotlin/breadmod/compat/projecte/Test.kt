package breadmod.compat.projecte

import moze_intel.projecte.api.ItemInfo
import moze_intel.projecte.api.proxy.IEMCProxy

class Test: IEMCProxy {
    override fun getValue(p0: ItemInfo): Long {
        TODO("IDFK")
    }

    override fun getSellValue(p0: ItemInfo): Long {
        TODO("Not yet implemented")
    }

    override fun getPersistentInfo(p0: ItemInfo): ItemInfo {
        TODO("Not yet implemented")
    }
}
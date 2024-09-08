package bread.mod.breadmod.fabric

import bread.mod.breadmod.ModMainCommon
import net.fabricmc.api.ModInitializer

class MainFabric : ModInitializer {
    override fun onInitialize() {
        ModMainCommon.init()
    }
}

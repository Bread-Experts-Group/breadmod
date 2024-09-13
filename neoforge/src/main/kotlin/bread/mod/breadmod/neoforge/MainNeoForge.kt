package bread.mod.breadmod.neoforge

import bread.mod.breadmod.ModMainCommon
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod

@Mod(ModMainCommon.MOD_ID)
class MainNeoForge(container: ModContainer) {
    init {
//        container.registerConfig(ModConfig.Type.COMMON, SPECIFICATION)
        ModMainCommon.init()
        ModMainCommon.initClient()
    }
}
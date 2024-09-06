package bread.mod.breadmod.neoforge

import bread.mod.breadmod.ModMainCommon
import bread.mod.breadmod.neoforge.config.CommonConfigSpecification.SPECIFICATION
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig

@Mod(ModMainCommon.MOD_ID)
class MainNeoForge(container: ModContainer) {
    init {
        container.registerConfig(ModConfig.Type.COMMON, SPECIFICATION)
        ModMainCommon.init()
        println("B")
    }
}

package bread.mod.breadmod.neoforge

import bread.mod.breadmod.ModMainCommon
import bread.mod.breadmod.neoforge.config.CommonConfigSpecification.SPECIFICATION
import bread.mod.breadmod.neoforge.registry.registerAllForge
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(ModMainCommon.MOD_ID)
class MainNeoForge(container: ModContainer) {
    init {
        container.registerConfig(ModConfig.Type.COMMON, SPECIFICATION)
        ModMainCommon.init()
        registerAllForge(MOD_BUS)
    }
}

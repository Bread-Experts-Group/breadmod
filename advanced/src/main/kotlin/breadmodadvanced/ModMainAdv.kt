package breadmodadvanced

import breadmod.ModMain.LOGGER
import breadmodadvanced.registry.registerAll
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.forge.MOD_BUS

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ModMainAdv.ID)
object ModMainAdv {
    const val ID = "breadmodadv"

    init {
        LOGGER.info("loaded breadmodadv")
        registerAll(MOD_BUS)
    }

}

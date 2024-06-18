package breadmodadvanced

import breadmodadvanced.registry.registerAll
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(ModMainAdv.ID)
internal object ModMainAdv {
    const val ID = "breadmodadv"
    val LOGGER: Logger = LogManager.getLogger(ID)

    init { registerAll(MOD_BUS) }
}

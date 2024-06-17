package breadmod.advanced

import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ModMain.ID)
object ModMain {
    const val ID = "breadmodadv"
    val LOGGER: Logger = LogManager.getLogger(ID)

    init {
        LOGGER.info("loaded breadmodadv")
    }

}

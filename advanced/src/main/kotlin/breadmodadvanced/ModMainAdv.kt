package breadmodadvanced

import breadmodadvanced.registry.registerAll
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import kotlin.reflect.full.functions

@Mod(ModMainAdv.ID)
internal object ModMainAdv {
    const val ID = "breadmodadv"
    val LOGGER: Logger = LogManager.getLogger(ID)

    init {
        Thread.currentThread().contextClassLoader.loadClass("breadmod.util.GeneralKt").kotlin.functions.forEach {
            println("HELP: ${it.name} ${it.parameters.joinToString()}")
        }
        registerAll(MOD_BUS)
    }
}

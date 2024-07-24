package breadmodadvanced

import breadmodadvanced.registry.registerAll
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(ModMainAdv.ID)
internal object ModMainAdv {
    const val ID = "breadmodadv"
    val LOGGER: Logger = LogManager.getLogger(ID)

    internal fun modLocation(vararg path: String, override: Boolean = false): ResourceLocation
            = path.toMutableList().let { ResourceLocation(if(override) it.removeFirst() else ID, it.joinToString("/")) }
    internal fun modTranslatable(type: String = "misc", vararg path: String, args: List<Any> = listOf()): MutableComponent
            = Component.translatable("$type.$ID.${path.joinToString(".")}", *args.toTypedArray())

    init {
        registerAll(MOD_BUS)
    }
}

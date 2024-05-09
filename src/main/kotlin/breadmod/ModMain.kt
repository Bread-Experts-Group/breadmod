package breadmod

import breadmod.registry.registerAll
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.common.data.LanguageProvider
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(ModMain.ID)
object ModMain {
    const val ID = "breadmod"
    val LOGGER: Logger = LogManager.getLogger(ID)

    /**
     * @param override Only use this when you need to refer to a namespace outside breadmod
     */
    fun modLocation(vararg path: String, override: Boolean = false): ResourceLocation
            = path.toMutableList().let { ResourceLocation(if(override) it.removeFirst() else ID, it.joinToString("/")) }
    fun modTranslatable(type: String = "misc", vararg path: String): MutableComponent
            = Component.translatable("$type.$ID.${path.joinToString(".")}")
    /**
     * Only use this for translatable strings for mods outside breadmod
     * @see modAddExt
     */
    fun modTranslatableExt(vararg path: String): MutableComponent
            = Component.translatable(path.joinToString("."))
    fun LanguageProvider.modAdd(value: String, type: String = "misc", vararg path: String)
            = add("$type.$ID.${path.joinToString(".")}", value)
    /**
     * Only use this for translatable strings for mods outside breadmod
     * @see modTranslatableExt
     */
    fun LanguageProvider.modAddExt(value: String, vararg path: String)
            = add(path.joinToString("."), value)

    init {
        LOGGER.info("Mod object initialized!")
        registerAll(MOD_BUS)
    }
}

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

@Mod(BreadMod.ID)
object BreadMod {
    const val ID = "breadmod"
    val LOGGER: Logger = LogManager.getLogger(ID)

    fun modLocation(path: String): ResourceLocation
            = ResourceLocation(ID, path)
    fun modTranslatable(type: String = "misc", vararg path: String): MutableComponent
            = Component.translatable("$type.$ID.${path.joinToString(".")}")
    fun LanguageProvider.modAdd(value: String, type: String = "misc", vararg path: String)
            = add("$type.$ID.${path.joinToString(".")}", value)

    init { registerAll(MOD_BUS) }
}

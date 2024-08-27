package breadmod

import breadmod.logging.ConsoleColorAppender
import breadmod.registry.registerAll
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.common.data.LanguageProvider
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.loading.FMLPaths
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.config.ConfigurationFactory
import org.apache.logging.log4j.core.config.Configurator
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(ModMain.ID)
internal object ModMain {
    const val ID = "breadmod"
    val LOGGER: Logger = LogManager.getLogger(ID)

    internal val DATA_DIR = FMLPaths.CONFIGDIR.get().resolve(ID)

    /**
     * @param override Only use this when you need to refer to a namespace outside breadmod
     */
    fun modLocation(vararg path: String, override: Boolean = false): ResourceLocation =
        path.toMutableList().let { ResourceLocation(if (override) it.removeFirst() else ID, it.joinToString("/")) }

    fun modTranslatable(type: String = "misc", vararg path: String, args: List<Any> = listOf()): MutableComponent =
        Component.translatable("$type.$ID.${path.joinToString(".")}", *args.toTypedArray())

    /**
     * Only use this for translatable strings for mods outside breadmod
     * @see modAddExt
     */
    private fun modTranslatableExt(vararg path: String): MutableComponent =
        Component.translatable(path.joinToString("."))

    fun LanguageProvider.modAdd(value: String, type: String = "misc", vararg path: String) =
        add("$type.$ID.${path.joinToString(".")}", value)

    /**
     * Only use this for translatable strings for mods outside breadmod
     * @see modTranslatableExt
     */
    fun LanguageProvider.modAddExt(value: String, vararg path: String) = add(path.joinToString("."), value)

    init {
        val ctx = LogManager.getContext(false) as LoggerContext
        val uri = this::class.java.getResource("/log4j2.xml")?.toURI() ?: throw IllegalStateException("Failed to load log4j2.xml")
        val cfg = ConfigurationFactory.getInstance().getConfiguration(ctx, ctx.name, uri, null)

        val clrApd = ConsoleColorAppender.createAppender("ConsoleColorAppender", null)
        cfg.addAppender(clrApd)
        Configurator.reconfigure(cfg)

        LOGGER.info("Mod object initialized!")
        registerAll(MOD_BUS)
    }
}

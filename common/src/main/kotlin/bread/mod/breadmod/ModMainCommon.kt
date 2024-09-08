package bread.mod.breadmod

import bread.mod.breadmod.logging.ConsoleColorAppender
import bread.mod.breadmod.registry.block.ModBlocks
import bread.mod.breadmod.registry.registerAll
import dev.architectury.platform.Platform
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.FireBlock
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.config.ConfigurationFactory
import org.apache.logging.log4j.core.config.Configurator

object ModMainCommon {
    const val MOD_ID: String = "breadmod"
    val LOGGER: Logger = LogManager.getLogger(MOD_ID)

    fun modLocation(vararg path: String, override: Boolean = false): ResourceLocation =
        path.toMutableList().let {
            ResourceLocation.fromNamespaceAndPath(if (override) it.removeFirst() else MOD_ID, it.joinToString("/"))
        }

    fun modTranslatable(type: String = "misc", vararg path: String, args: List<Any> = listOf()): MutableComponent =
        Component.translatable("$type.$MOD_ID.${path.joinToString(".")}", *args.toTypedArray())

    fun init() {
        val ctx = LogManager.getContext(false) as LoggerContext
        val uri = this::class.java.getResource("/log4j2.xml")?.toURI() ?: throw IllegalStateException("Failed to load log4j2.xml")
        val cfg = ConfigurationFactory.getInstance().getConfiguration(ctx, ctx.name, uri, null)

        val clrApd = ConsoleColorAppender.createAppender("ConsoleColorAppender", null)
        cfg.addAppender(clrApd)
        Configurator.reconfigure(cfg)

        val fireBlock = Blocks.FIRE as FireBlock

        // Register all our mod contents
        registerAll()

//        FuelRegistry.register(1600 * 9, ModBlocks.CHARCOAL_BLOCK.get())

        if (Platform.isFabric()) {
            fireBlock.setFlammable(ModBlocks.CHARCOAL_BLOCK.get().block, 30, 15)
        } else {
            // todo figure out why forge is throwing registry object not present here
        }
    }
}

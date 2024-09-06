package bread.mod.breadmod

import bread.mod.breadmod.logging.ConsoleColorAppender
import bread.mod.breadmod.registry.Registry
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.config.ConfigurationFactory
import org.apache.logging.log4j.core.config.Configurator

object ModMainCommon {
    const val MOD_ID: String = "breadmod"

    fun init() {
        val ctx = LogManager.getContext(false) as LoggerContext
        val uri = this::class.java.getResource("/log4j2.xml")?.toURI() ?: throw IllegalStateException("Failed to load log4j2.xml")
        val cfg = ConfigurationFactory.getInstance().getConfiguration(ctx, ctx.name, uri, null)

        val clrApd = ConsoleColorAppender.createAppender("ConsoleColorAppender", null)
        cfg.addAppender(clrApd)
        Configurator.reconfigure(cfg)

        Registry.registerAll()
    }
}

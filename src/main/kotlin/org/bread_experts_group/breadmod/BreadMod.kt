package org.bread_experts_group.breadmod

import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.fml.loading.FMLLoader
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.config.ConfigurationFactory
import org.apache.logging.log4j.core.config.Configurator
import org.bread_experts_group.breadmod.data_holders.common.ToolGunData
import org.bread_experts_group.breadmod.registry.ModConfiguration
import org.bread_experts_group.breadmod.registry.Registry
import org.bread_experts_group.coder.format.riff.RIFFInputStream

/**
 * Main mod class.
 */
@Mod(BreadMod.ID)
class BreadMod(eventBus: IEventBus, container: ModContainer) {
	companion object {
		const val ID: String = "breadmod"
		private val logger: Logger = LogManager.getLogger("Bread Mod Main")

		fun modModelLoc(id: String): ModelResourceLocation = ModelResourceLocation.standalone(
			this.modLocation(id)
		)

		/**
		 * @param override Only use this when you need to refer to a namespace outside breadmod
		 */
		fun modLocation(vararg path: String, override: Boolean = false): ResourceLocation =
			path.toMutableList().let {
				ResourceLocation.fromNamespaceAndPath(
					if (override) it.removeFirst() else this.ID, it.joinToString("/")
				)
			}

		fun modTranslatable(
			type: String = "misc",
			vararg path: String,
			args: List<Any> = listOf()
		): MutableComponent = Component.translatable(
			"$type.${this.ID}.${path.joinToString(".")}",
			*args.toTypedArray()
		)
	}

	init {
		if (!FMLLoader.isProduction() || System.getProperty("breadmod.logging") == "true") {
			val context = LogManager.getContext(false) as LoggerContext
			val fileLocator = this::class.java.getResource("/log4j2.xml")?.toURI()
				?: throw IllegalStateException("Failed to load log4j2.xml")
			val configuration = ConfigurationFactory
				.getInstance()
				.getConfiguration(
					context,
					context.name,
					fileLocator,
					null
				)
			val colorAppender = ConsoleColorAppender.createAppender("ConsoleColorAppender", null)
			configuration.addAppender(colorAppender)
			Configurator.reconfigure(configuration)
		}
		this::class.java.getResourceAsStream("/Hoshi ni Natte.wav")?.let {
			RIFFInputStream(it).readAllParsed().forEach(logger::info)
		}
		Companion.logger.info("Hello world!")

		container.registerConfig(ModConfig.Type.COMMON, ModConfiguration.COMMON_SPEC.right, "breadmod-common.toml")
		container.registerConfig(ModConfig.Type.CLIENT, ModConfiguration.CLIENT_SPEC.right, "breadmod-client.toml")

		Registry.registerAll(eventBus)
		ToolGunData.loadToolGunModes()
	}
}

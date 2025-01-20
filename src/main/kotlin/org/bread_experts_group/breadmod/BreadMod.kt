package org.bread_experts_group.breadmod

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.ModContainer
import net.neoforged.fml.ModList
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.fml.loading.FMLLoader
import net.neoforged.fml.loading.modscan.ModAnnotation.EnumHolder
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.config.ConfigurationFactory
import org.apache.logging.log4j.core.config.Configurator
import org.bread_experts_group.breadmod.CommonNeoForgeEventBus.toolGunModes
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.api.IToolGunModeClient
import org.bread_experts_group.breadmod.api.ToolGunMode
import org.bread_experts_group.breadmod.client.tool_gun.render.ToolGunClientGlobals.toolGunModesClient
import org.bread_experts_group.breadmod.logging.ConsoleColorAppender
import org.bread_experts_group.breadmod.logging.ConsoleUnnamedRedirection
import org.bread_experts_group.breadmod.registry.ModConfiguration
import org.bread_experts_group.breadmod.registry.Registry
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import java.lang.annotation.ElementType
import kotlin.reflect.full.createInstance

/**
 * Main mod class.
 */
@Mod(BreadMod.ID)
class BreadMod(container: ModContainer) {
	companion object {
		const val ID: String = "breadmod"

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
		// todo singleplayer prod shows modes on screen fine, can't change modes. only client renderer changes
		//  multiplayer prod seems to work fine
		//  dev works fine on singleplayer
		/**
		 * Loads tool gun modes on their respective [Dist].
		 */
		fun loadToolGunModesSided() {
			val currentDist = FMLLoader.getDist()
			ModList.get().allScanData.forEach { scanData ->
				scanData.getAnnotatedBy(ToolGunMode::class.java, ElementType.TYPE).forEach { annotationData ->
					val holder = annotationData.annotationData["side"] as EnumHolder
					val mode = Class.forName(annotationData.memberName).kotlin
					if (holder.value == "DEDICATED_SERVER" && currentDist == Dist.DEDICATED_SERVER) {
						val serverMode = mode.createInstance() as IToolGunMode
						toolGunModes[serverMode.getUid()] = serverMode
					} else if (holder.value == "CLIENT" && currentDist == Dist.CLIENT) {
						val clientMode = mode.createInstance() as IToolGunModeClient
						toolGunModesClient[clientMode.getUid()] = clientMode
					}
				}
			}
		}
	}

	val logger: Logger = LogManager.getLogger()

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

			ConsoleUnnamedRedirection.setup()
		}

		this.logger.log(Level.INFO, "Hello world!")

		container.registerConfig(ModConfig.Type.COMMON, ModConfiguration.COMMON_SPEC.right, "breadmod-common.toml")
		container.registerConfig(ModConfig.Type.CLIENT, ModConfiguration.CLIENT_SPEC.right, "breadmod-client.toml")
		// Register the KDeferredRegister to the mod-specific event bus
		Registry.registerAll(MOD_BUS)
	}
}

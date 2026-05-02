package org.bread_experts_group.breadmod

import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.config.ModConfig
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.data_holders.common.ToolGunData
import org.bread_experts_group.breadmod.registry.ModConfiguration
import org.bread_experts_group.breadmod.registry.Registry
import org.bread_experts_group.breadmod.registry.item.integration.ModIntegrationItems
import org.bread_experts_group.upwards.UpwardsMod

/**
 * Main mod class.
 */
@UpwardsMod(modID = BreadMod.ID, dependencyLocation = "libs")
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
		Companion.logger.info("Hello world!")

		container.registerConfig(ModConfig.Type.COMMON, ModConfiguration.COMMON_SPEC.right, "breadmod-common.toml")
		container.registerConfig(ModConfig.Type.CLIENT, ModConfiguration.CLIENT_SPEC.right, "breadmod-client.toml")

		Registry.registerAll(eventBus)
		ModIntegrationItems.registerAll()
		ToolGunData.initializeToolGunModes()
	}
}

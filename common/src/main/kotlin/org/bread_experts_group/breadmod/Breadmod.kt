package org.bread_experts_group.breadmod

import net.minecraft.resources.ResourceLocation
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadlib.data.LocaleGenerator
import org.bread_experts_group.breadlib.data.ModelGenerator
import org.bread_experts_group.breadlib.registry.RegistryProvider
import org.bread_experts_group.breadlib.task.TaskManager
import org.bread_experts_group.breadlib.task.data.GenerateDataTask
import org.bread_experts_group.breadlib.task.data.RegistrySetBuilderTask
import org.bread_experts_group.breadmod.data.ModRecipeProvider
import org.bread_experts_group.breadmod.registry.ModBlocks
import org.bread_experts_group.breadmod.registry.ModCreativeTabs
import org.bread_experts_group.breadmod.registry.ModItems
import java.util.Locale

class Breadmod {
    companion object {
        const val MOD_ID: String = "breadmod"
        val LOGGER: Logger = LogManager.getLogger("Breadmod")

        fun modLoc(vararg path: String): ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(MOD_ID, path.joinToString("/"))

        fun init() {
            RegistryProvider.initializeProviders(ModBlocks.REGISTRY, ModItems.REGISTRY, ModCreativeTabs.REGISTRY)

            TaskManager.newTask { task: GenerateDataTask ->
                task.addGenerator(
                    ModelGenerator(MOD_ID)
                        .flat3D(ModBlocks.BREAD_BLOCK)
                        .blockState(ModBlocks.BREAD_BLOCK)
                        .model2D(ModBlocks.BREAD_BLOCK)
                )
                task.addGenerator(
                    LocaleGenerator(MOD_ID, Locale.of("en", "us"))
                        .addBLBlocks(ModBlocks.BREAD_BLOCK to "Bread Block")
                        .addBLCreativeTabs(ModCreativeTabs.TEST_TAB to "Breadmod")
                )
                task.addProvider(ModRecipeProvider(task.packOutput, task.registries), MOD_ID)
            }

            TaskManager.newTask { task: RegistrySetBuilderTask ->
                task.addBuilder(MOD_ID) { builder ->
                    // todo
                }
            }
        }
    }
}
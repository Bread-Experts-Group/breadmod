package org.bread_experts_group.breadmod

import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadlib.data.LocaleProvider
import org.bread_experts_group.breadlib.data.ModelProvider
import org.bread_experts_group.breadlib.data.SoundProvider
import org.bread_experts_group.breadlib.registry.RegistryProvider
import org.bread_experts_group.breadlib.task.TaskManager
import org.bread_experts_group.breadlib.task.data.GenerateDataTask
import org.bread_experts_group.breadlib.task.data.RegistrySetBuilderTask
import org.bread_experts_group.breadmod.data.ModRecipeProvider
import org.bread_experts_group.breadmod.registry.ModBlocks
import org.bread_experts_group.breadmod.registry.ModCreativeTabs
import org.bread_experts_group.breadmod.registry.ModItems
import org.bread_experts_group.breadmod.registry.ModSounds
import org.bread_experts_group.breadmod.registry.bootstrap.ModRecords
import java.util.Locale

class Breadmod {
    companion object {
        const val MOD_ID: String = "breadmod"
        val LOGGER: Logger = LogManager.getLogger("Breadmod")

        fun modLoc(vararg path: String): ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(MOD_ID, path.joinToString("/"))

        fun translatable(
            type: String = "misc",
            vararg path: String,
            args: List<Any> = listOf()
        ): MutableComponent = Component.translatable(
            "$type.${this.MOD_ID}.${path.joinToString(".")}",
            *args.toTypedArray()
        )

        fun init() {
            RegistryProvider.initializeProviders(
                ModBlocks.REGISTRY,
                ModItems.REGISTRY,
                ModCreativeTabs.REGISTRY,
                ModSounds.REGISTRY
            )

            TaskManager.newTask { task: GenerateDataTask ->
                task.addProvider(
                    ModelProvider(MOD_ID, task.packOutput)
                        .flat3D(ModBlocks.BREAD_BLOCK)
                        .blockState(ModBlocks.BREAD_BLOCK)
                        .model2D(ModBlocks.BREAD_BLOCK)
                        .flat2D(ModItems.RECORD_SECRET_HOPPIN)
                )
                task.addProvider(
                    LocaleProvider(Locale.of("en", "us"), MOD_ID, task.packOutput)
                        .addBLBlocks(ModBlocks.BREAD_BLOCK to "Bread Block")
                        .addBLCreativeTabs(ModCreativeTabs.TEST_TAB to "Breadmod")
                )
                task.addProvider(ModRecipeProvider(task.packOutput, task.registries), MOD_ID)
                task.addProvider(
                    SoundProvider(MOD_ID, task.packOutput)
                        .sound("secret_hoppin", ModSounds.SECRET_HOPPIN)
                )
            }

            TaskManager.newTask { task: RegistrySetBuilderTask ->
                task.addBuilder(MOD_ID) { builder ->
                    builder.add(Registries.JUKEBOX_SONG, ModRecords::bootstrap)
                }
            }
        }
    }
}
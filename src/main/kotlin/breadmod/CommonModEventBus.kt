package breadmod

import breadmod.ModMain.LOGGER
import breadmod.compat.curios.ModCuriosSlotsProvider
import breadmod.compat.projecte.ModEMCProvider
import breadmod.datagen.*
import breadmod.datagen.lang.USEnglishLanguageProvider
import breadmod.datagen.tag.ModBlockTags
import breadmod.datagen.tag.ModFluidTags
import breadmod.datagen.tag.ModItemTags
import breadmod.datagen.tag.ModPaintingTags
import breadmod.datagen.tool_gun.ModToolGunModeProvider
import breadmod.network.PacketHandler.NETWORK
import breadmod.registry.block.ModBlocks
import breadmod.registry.worldgen.dimensions.ModBiomes
import breadmod.registry.worldgen.dimensions.ModDimensions
import breadmod.registry.worldgen.dimensions.ModFeatures
import breadmod.registry.worldgen.dimensions.ModNoiseGenerators
import breadmod.registry.worldgen.structures.ModPools
import breadmod.registry.worldgen.structures.ModStructureSets
import breadmod.registry.worldgen.structures.ModStructures
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstapContext
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider
import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent

typealias BootstrapContext<T> = BootstapContext<T>

@Suppress("unused")
@Mod.EventBusSubscriber(modid = ModMain.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object CommonModEventBus {
    // Data Generation
    @SubscribeEvent
    fun gatherData(event: GatherDataEvent) {
        val generator = event.generator
        val packOutput = generator.packOutput
        val existingFileHelper = event.existingFileHelper
        val lookupProvider = event.lookupProvider

        generator.addProvider(true, ModToolGunModeProvider(packOutput))

        if (event.includeServer()) {
            LOGGER.info("Server data-gen")
            generator.addProvider(true, constructLootProvider(ModBlocks.ModBlockLoot(), packOutput))
            generator.addProvider(true, ModRecipeProvider(packOutput))
            generator.addProvider(true, ModEMCProvider(packOutput, lookupProvider))

            val blockTagGenerator =
                generator.addProvider(true, ModBlockTags(packOutput, lookupProvider, existingFileHelper))
            generator.addProvider(
                true,
                ModItemTags(packOutput, lookupProvider, blockTagGenerator.contentsGetter(), existingFileHelper)
            )
            generator.addProvider(true, ModPaintingTags(packOutput, lookupProvider, existingFileHelper))
            generator.addProvider(true, ModFluidTags(packOutput, lookupProvider, existingFileHelper))
            generator.addProvider(true, ModCuriosSlotsProvider(packOutput, existingFileHelper, lookupProvider))

            generator.addProvider(
                true, DatapackBuiltinEntriesProvider(
                    packOutput, lookupProvider, RegistrySetBuilder()
                        .add(Registries.TEMPLATE_POOL, ModPools::bootstrap)
                        .add(Registries.STRUCTURE, ModStructures::bootstrap)
                        .add(Registries.STRUCTURE_SET, ModStructureSets::bootstrap)

                        .add(Registries.NOISE_SETTINGS, ModNoiseGenerators::bootstrapNoiseGenerators)

                        .add(Registries.CONFIGURED_FEATURE, ModFeatures::bootstrapConfiguredFeatures)
                        .add(Registries.PLACED_FEATURE, ModFeatures::bootstrapPlacedFeatures)

                        .add(Registries.BIOME, ModBiomes::bootstrapBiomes)
                        .add(Registries.DIMENSION_TYPE, ModDimensions::bootstrapDimensionTypes)
                        .add(Registries.LEVEL_STEM, ModDimensions::bootstrapLevelStems),
                    setOf(ModMain.ID)
                )
            )
        }
        if (event.includeClient()) {
            LOGGER.info("Client data-gen")
            generator.addProvider(true, USEnglishLanguageProvider(packOutput, "en_us"))
            generator.addProvider(true, ModBlockStateAndModelProvider(packOutput, existingFileHelper))
            generator.addProvider(true, ModSoundDefinitionsProvider(packOutput, existingFileHelper))
            generator.addProvider(true, ModItemModelProvider(packOutput, existingFileHelper))
        }
    }

    @SubscribeEvent
    fun onCommonSetup(@Suppress("UNUSED_PARAMETER") event: FMLCommonSetupEvent) {
        LOGGER.info("Common setup")
        NETWORK
    }
}
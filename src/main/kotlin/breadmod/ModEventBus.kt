package breadmod

import breadmod.BreadMod.LOGGER
import breadmod.BreadMod.modLocation
import breadmod.block.color.BlackbodyBlockColor
import breadmod.block.entity.menu.DoughMachineScreen
import breadmod.block.entity.renderer.BlackbodyRenderer
import breadmod.block.entity.renderer.SidedScreenRenderer
import breadmod.compat.projecte.ModEMCProvider
import breadmod.datagen.*
import breadmod.datagen.dimension.ModDimensions
import breadmod.datagen.dimension.worldgen.ModBiomes
import breadmod.datagen.dimension.worldgen.ModFeatures
import breadmod.datagen.dimension.worldgen.ModNoiseGenerators
import breadmod.datagen.lang.USEnglishLanguageProvider
import breadmod.datagen.tags.ModBlockTags
import breadmod.datagen.tags.ModItemTags
import breadmod.datagen.tags.ModPaintingTags
import breadmod.entity.renderer.PrimedHappyBlockRenderer
import breadmod.item.armor.BreadArmorItem
import breadmod.item.colors.ArmorColor
import breadmod.network.PacketHandler.NETWORK
import breadmod.registry.block.ModBlockEntities
import breadmod.registry.block.ModBlocks
import breadmod.registry.entity.ModEntities.HAPPY_BLOCK_ENTITY
import breadmod.registry.item.ModItems
import breadmod.registry.screen.ModMenuTypes
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.Util
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraftforge.client.event.EntityRenderersEvent
import net.minecraftforge.client.event.RegisterColorHandlersEvent
import net.minecraftforge.client.event.RegisterShadersEvent
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider
import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import java.util.function.Function


@Suppress("unused")
@Mod.EventBusSubscriber(modid = BreadMod.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object ModEventBus {
    // Data Generation
    @SubscribeEvent
    fun gatherData(event: GatherDataEvent) {
        val generator = event.generator
        val packOutput = generator.packOutput
        val existingFileHelper = event.existingFileHelper
        val lookupProvider = event.lookupProvider

        if(event.includeServer()) {
            LOGGER.info("Server datagen")
            generator.addProvider(true, ModLootTableProvider.create(packOutput))
            generator.addProvider(true, ModRecipeProvider(packOutput))
            generator.addProvider(true, ModEMCProvider(packOutput, lookupProvider))

            val blockTagGenerator = generator.addProvider(true, ModBlockTags(packOutput, lookupProvider, existingFileHelper))
            generator.addProvider(true, ModItemTags(packOutput, lookupProvider, blockTagGenerator.contentsGetter(), existingFileHelper))
            generator.addProvider(true, ModPaintingTags(packOutput, lookupProvider, existingFileHelper))

            generator.addProvider(true, DatapackBuiltinEntriesProvider(
                packOutput, lookupProvider, RegistrySetBuilder()
                    .add(Registries.NOISE_SETTINGS, ModNoiseGenerators::bootstrapNoiseGenerators)

                    .add(Registries.CONFIGURED_FEATURE, ModFeatures::bootstrapConfiguredFeatures)
                    .add(Registries.PLACED_FEATURE, ModFeatures::bootstrapPlacedFeatures)

                    .add(Registries.BIOME, ModBiomes::bootstrapBiomes)
                    .add(Registries.DIMENSION_TYPE, ModDimensions::bootstrapDimensionTypes)
                    .add(Registries.LEVEL_STEM, ModDimensions::bootstrapLevelStems),
                setOf(BreadMod.ID))
            )
        }
        if(event.includeClient()) {
            LOGGER.info("Client datagen")
            generator.addProvider(true, USEnglishLanguageProvider(packOutput, BreadMod.ID, "en_us"))
            generator.addProvider(true, ModBlockStateProvider(packOutput, BreadMod.ID, existingFileHelper))
            generator.addProvider(true, ModSoundDefinitionsProvider(packOutput, BreadMod.ID, existingFileHelper))
            generator.addProvider(true, ModItemModelProvider(packOutput, BreadMod.ID, existingFileHelper))
        }
    }

    @SubscribeEvent
    fun onClientSetup(event: FMLClientSetupEvent) {
        val blockingProperty = modLocation("blocking")

        event.enqueueWork {
            ItemProperties.register(
                ModItems.BREAD_SHIELD.get(), blockingProperty) { itemStack: ItemStack, _: ClientLevel?, livingEntity: LivingEntity?, _: Int ->
                if (livingEntity != null && livingEntity.isUsingItem && livingEntity.useItem == itemStack) 1.0f else 0.0f
            }

            MenuScreens.register(ModMenuTypes.DOUGH_MACHINE.get()) { pMenu, pInventory, pTitle -> DoughMachineScreen(pMenu,pInventory,pTitle) }
        }
    }

    @SubscribeEvent
    fun onCommonSetup(event: FMLCommonSetupEvent) {
        NETWORK
    }

    @SubscribeEvent
    fun registerBlockColors(event: RegisterColorHandlersEvent.Block) {
        event.register(BlackbodyBlockColor, ModBlocks.HEATING_ELEMENT_BLOCK.get().block)
    }
    @SubscribeEvent
    fun registerItemColors(event: RegisterColorHandlersEvent.Item) {
        event.register(ArmorColor, *ModItems.deferredRegister.entries.mapNotNull {
            val armorItem = it.get()
            if(armorItem is BreadArmorItem) armorItem else null
        }.toTypedArray())
    }

    @SubscribeEvent
    fun registerRenders(event: EntityRenderersEvent.RegisterRenderers) {
        event.registerEntityRenderer(HAPPY_BLOCK_ENTITY.get()) { pContext: EntityRendererProvider.Context ->
            PrimedHappyBlockRenderer(pContext) }
    }

    @SubscribeEvent
    fun registerBlockEntityRenderers(event: EntityRenderersEvent.RegisterRenderers) {
        event.registerBlockEntityRenderer(ModBlockEntities.HEATING_ELEMENT.get()) { BlackbodyRenderer() }
        event.registerBlockEntityRenderer(ModBlockEntities.MONITOR.get()) { SidedScreenRenderer() }
    }

    lateinit var loadedShader: ShaderInstance
    @SubscribeEvent
    fun registerShaders(event: RegisterShadersEvent) {
        event.registerShader(ShaderInstance(event.resourceProvider, modLocation("projector"), DefaultVertexFormat.NEW_ENTITY)) { shader ->
            loadedShader = shader
        }
    }

    abstract class A(
        pName: String,
        pFormat: VertexFormat,
        pMode: VertexFormat.Mode,
        pBufferSize: Int,
        pAffectsCrumbling: Boolean,
        pSortOnUpload: Boolean,
        pSetupState: Runnable,
        pClearState: Runnable
    ) : RenderType(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState) {
        companion object {
            // Holds the object loaded via RegisterShadersEvent
            private val brightSolidShader: ShaderInstance? = null

            // Shader state for use in the render type, the supplier ensures it updates automatically with resource reloads
            private val RENDERTYPE_BRIGHT_SOLID_SHADER = ShaderStateShard { brightSolidShader }

            // The memoize caches the output value for each input, meaning the expensive registration process doesn't have to rerun
            var BRIGHT_SOLID: Function<ResourceLocation, RenderType> = Util.memoize { locationIn: ResourceLocation ->
                brightSolid(
                    locationIn
                )
            }

            private fun brightSolid(locationIn: ResourceLocation): RenderType {
                val `rendertype$state`: CompositeState = CompositeState.builder()
                    .setShaderState(RENDERTYPE_BRIGHT_SOLID_SHADER)
                    .setTextureState(TextureStateShard(locationIn, false, false))
                    .setTransparencyState(NO_TRANSPARENCY)
                    .setLightmapState(NO_LIGHTMAP)
                    .setOverlayState(NO_OVERLAY)
                    .createCompositeState(true)
                return create(
                    "gbook_bright_solid",
                    DefaultVertexFormat.NEW_ENTITY,
                    VertexFormat.Mode.QUADS,
                    256,
                    true,
                    false,
                    `rendertype$state`
                )
            }
        }
    }
}
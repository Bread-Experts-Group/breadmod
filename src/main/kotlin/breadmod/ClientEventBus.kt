package breadmod

import breadmod.BreadMod.LOGGER
import breadmod.datagen.ModBlockState
import breadmod.datagen.ModItemModels
import breadmod.datagen.ModLootTableProvider.create
import breadmod.datagen.ModRecipe
import breadmod.datagen.ModSounds.ModSoundsDatagen
import breadmod.datagen.lang.USEnglishLanguageProvider
import breadmod.datagen.tags.ModBlockTags
import breadmod.datagen.tags.ModItemTags
import breadmod.entity.ModEntities.HAPPY_BLOCK_ENTITY
import breadmod.entity.renderer.PrimedHappyBlockRenderer
import breadmod.item.ModItems
import breadmod.screens.BreadFurnaceScreen
import breadmod.screens.ModMenuTypes
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.data.DataProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers
import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

@Suppress("SpellCheckingInspection", "unused")
@Mod.EventBusSubscriber(modid = BreadMod.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object ClientEventBus {
    // Data Generation
    @SubscribeEvent
    fun gatherData(event: GatherDataEvent) {
        val generator = event.generator
        val packOutput = generator.packOutput
        val existingFileHelper = event.existingFileHelper
        val lookupProvider = event.lookupProvider

        // Server side data generation
        generator.addProvider(event.includeServer(), create(packOutput))
        generator.addProvider(event.includeServer(),
            DataProvider.Factory { pOutput -> ModRecipe(pOutput) } as DataProvider.Factory<ModRecipe>)

        LOGGER.info("Generating mod tags")
        val blockTagGenerator = generator.addProvider(event.includeServer(), ModBlockTags(packOutput, lookupProvider, existingFileHelper))
        generator.addProvider(event.includeServer(), ModItemTags(packOutput, lookupProvider, blockTagGenerator.contentsGetter(), existingFileHelper))

        // Client side data generation
        LOGGER.info("Generating lang file")
        generator.addProvider(event.includeClient(), USEnglishLanguageProvider(packOutput, BreadMod.ID, "en_us"))
        LOGGER.info("Generating mod blockstates")
        generator.addProvider(event.includeClient(), ModBlockState(packOutput, BreadMod.ID, existingFileHelper))
        LOGGER.info("Generating sound file")
        generator.addProvider(event.includeClient(), ModSoundsDatagen(packOutput, BreadMod.ID, existingFileHelper))
        LOGGER.info("Generating item models")
        generator.addProvider(event.includeClient(), ModItemModels(packOutput, BreadMod.ID, existingFileHelper))
    }

    @SubscribeEvent
    fun registerRenders(event: RegisterRenderers) {
        event.registerEntityRenderer(HAPPY_BLOCK_ENTITY.get()) { pContext: EntityRendererProvider.Context ->
            PrimedHappyBlockRenderer(pContext) }
    }

    // Client Stuff
    private val BLOCKING_PROPERTY_RESLOC = ResourceLocation(BreadMod.ID, "blocking")
    @SubscribeEvent
    fun onClientSetup(event: FMLClientSetupEvent) {
        event.enqueueWork {
            ItemProperties.register(
                ModItems.BREAD_SHIELD.get(), BLOCKING_PROPERTY_RESLOC) { itemStack: ItemStack, _: ClientLevel?, livingEntity: LivingEntity?, _: Int ->
                if (livingEntity != null && livingEntity.isUsingItem && livingEntity.useItem == itemStack) 1.0f else 0.0f
            }

            MenuScreens.register(ModMenuTypes.BREAD_FURNACE.get()) { pMenu, pInventory, pTitle ->
                BreadFurnaceScreen(pMenu,pInventory,pTitle) }
        }
    }
}
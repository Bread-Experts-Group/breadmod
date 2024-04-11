package breadmod

import breadmod.BreadMod.LOGGER
import breadmod.BreadMod.modLocation
import breadmod.block.color.BlackbodyBlockColor
import breadmod.block.entity.renderer.BlackbodyRenderer
import breadmod.datagen.lang.USEnglishLanguageProvider
import breadmod.registry.entity.ModEntities.HAPPY_BLOCK_ENTITY
import breadmod.entity.renderer.PrimedHappyBlockRenderer
import breadmod.registry.item.ModItems
import breadmod.block.entity.menu.BreadFurnaceScreen
import breadmod.datagen.*
import breadmod.datagen.tags.ModBlockTags
import breadmod.datagen.tags.ModItemTags
import breadmod.datagen.tags.ModPaintingTags
import breadmod.registry.block.ModBlockEntities
import breadmod.registry.block.ModBlocks
import breadmod.registry.screen.ModMenuTypes
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraftforge.client.event.EntityRenderersEvent
import net.minecraftforge.client.event.RegisterColorHandlersEvent
import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

@Suppress("unused")
@Mod.EventBusSubscriber(modid = BreadMod.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object ModEventBus {
    // Data Generation
    @SubscribeEvent
    fun gatherData(event: GatherDataEvent) {
        LOGGER.info("SORRY SORRY SORRY")

        val generator = event.generator
        val packOutput = generator.packOutput
        val existingFileHelper = event.existingFileHelper
        val lookupProvider = event.lookupProvider

        if(event.includeServer()) {
            LOGGER.info("Server datagen")
            generator.addProvider(true, ModLootTableProvider.create(packOutput))
            generator.addProvider(true, ModRecipeProvider(packOutput))

            val blockTagGenerator = generator.addProvider(true, ModBlockTags(packOutput, lookupProvider, existingFileHelper))
            generator.addProvider(true, ModItemTags(packOutput, lookupProvider, blockTagGenerator.contentsGetter(), existingFileHelper))
            generator.addProvider(true, ModPaintingTags(packOutput, lookupProvider, existingFileHelper))
        } else if(event.includeClient()) {
            LOGGER.info("Client datagen")
            generator.addProvider(true, USEnglishLanguageProvider(packOutput, BreadMod.ID, "en_us"))
            generator.addProvider(true, ModBlockStateProvider(packOutput, BreadMod.ID, existingFileHelper))
            //generator.addProvider(true, ModSoundDefinitions(packOutput, MOD_ID, existingFileHelper))
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

            MenuScreens.register(ModMenuTypes.BREAD_FURNACE.get()) { pMenu, pInventory, pTitle -> BreadFurnaceScreen(pMenu,pInventory,pTitle) }
        }
    }

    @SubscribeEvent
    fun registerBlockColors(event: RegisterColorHandlersEvent.Block) {
        event.register(BlackbodyBlockColor, ModBlocks.HEATING_ELEMENT_BLOCK.get().block)
    }

    @SubscribeEvent
    fun registerRenders(event: EntityRenderersEvent.RegisterRenderers) {
        event.registerEntityRenderer(HAPPY_BLOCK_ENTITY.get()) { pContext: EntityRendererProvider.Context ->
            PrimedHappyBlockRenderer(pContext) }
    }

    @SubscribeEvent
    fun registerBlockEntityRenderers(event: EntityRenderersEvent.RegisterRenderers) {
        event.registerBlockEntityRenderer(ModBlockEntities.HEATING_ELEMENT.get()) { BlackbodyRenderer() }
    }
}
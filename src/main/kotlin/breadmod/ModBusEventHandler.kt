package breadmod

import breadmod.datagen.ModBlockState
import breadmod.datagen.ModItemModels
import breadmod.datagen.ModLootTableProvider.create
import breadmod.datagen.ModRecipe
import breadmod.datagen.ModSounds.ModSoundsDatagen
import breadmod.datagen.lang.USEnglishLanguageProvider
import breadmod.datagen.tags.ModBlockTags
import breadmod.datagen.tags.ModItemTags
import breadmod.item.ModItems
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import org.apache.logging.log4j.Level

@Suppress("SpellCheckingInspection", "unused")
@Mod.EventBusSubscriber(modid = BreadMod.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object ModBusEventHandler {
    // Data Generation
    @SubscribeEvent
    fun gatherData(event: GatherDataEvent) {
        val generator = event.generator
        val packOutput = generator.packOutput
        val existingFileHelper = event.existingFileHelper
        val lookupProvider = event.lookupProvider

        // Server side data generation
        create(packOutput)?.let { generator.addProvider(event.includeServer(), it) }
        generator.addProvider(event.includeServer(),
            DataProvider.Factory { pOutput: PackOutput? -> ModRecipe(pOutput) } as DataProvider.Factory<ModRecipe>)

        BreadMod.LOGGER.log(Level.INFO, "Generating mod tags")
        val blockTagGenerator = generator.addProvider(event.includeServer(), ModBlockTags(packOutput, lookupProvider, existingFileHelper))
        generator.addProvider(event.includeServer(), ModItemTags(packOutput, lookupProvider, blockTagGenerator.contentsGetter(), existingFileHelper))

        // Client side data generation
        BreadMod.LOGGER.log(Level.INFO, "Generating lang file")
        generator.addProvider(event.includeClient(), USEnglishLanguageProvider(packOutput, BreadMod.ID, "en_us"))
        BreadMod.LOGGER.log(Level.INFO, "Generating mod blockstates")
        generator.addProvider(event.includeClient(), ModBlockState(packOutput, BreadMod.ID, existingFileHelper))
        BreadMod.LOGGER.log(Level.INFO, "Generating sound file")
        generator.addProvider(event.includeClient(), ModSoundsDatagen(packOutput, BreadMod.ID, existingFileHelper))
        BreadMod.LOGGER.log(Level.INFO, "Generating item models")
        generator.addProvider(event.includeClient(), ModItemModels(packOutput, BreadMod.ID, existingFileHelper))
    }

    // Client Stuff
    private val BLOCKING_PROPERTY_RESLOC = ResourceLocation(BreadMod.ID, "blocking")
    @SubscribeEvent
    fun onClientSetup(event: FMLClientSetupEvent) {
        event.enqueueWork {
            ItemProperties.register(
                ModItems.BREAD_SHIELD, BLOCKING_PROPERTY_RESLOC) { itemStack: ItemStack, _: ClientLevel?, livingEntity: LivingEntity?, _: Int ->
                if (livingEntity != null && livingEntity.isUsingItem && livingEntity.useItem == itemStack) 1.0f else 0.0f
            }
        }
    }
}
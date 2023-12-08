package breadmod

import breadmod.BreadMod.LOGGER
import breadmod.datagen.provider.*
import breadmod.datagen.provider.lang.USEnglishLanguageProvider
import breadmod.datagen.provider.loot.ModLootTables
import breadmod.item.ModItems
import breadmod.recipes.ModRecipes
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

@Suppress("SpellCheckingInspection")
@EventBusSubscriber(modid = BreadMod.ID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object ModBusEventHandler {
    // Data Generation
    @SubscribeEvent
    fun gatherData(event: GatherDataEvent) {
        event.generator.let {
            if(event.includeClient()) {
                LOGGER.debug("Data generation: client")
                it.addProvider(true, ItemModels(it, event.existingFileHelper))
                it.addProvider(true, BlockModels(it, event.existingFileHelper))
                it.addProvider(true, BlockStates(it, event.existingFileHelper))
                it.addProvider(true, ModSounds.Generator(it, event.existingFileHelper))
                it.addProvider(true, USEnglishLanguageProvider(it))
                LOGGER.debug("Data generation: client (finished)")
            }

            if(event.includeServer()) {
                LOGGER.debug("Data generation: server")
                it.addProvider(true, ModRecipes.Generator(it))
                it.addProvider(true, ModLootTables(it))
                LOGGER.debug("Data generation: server (finished)")
            }
        }

        LOGGER.debug("Data generation finished")
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
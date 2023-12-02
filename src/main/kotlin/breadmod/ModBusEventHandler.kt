package breadmod

import breadmod.BreadMod.LOGGER
import breadmod.datagen.provider.*
import breadmod.datagen.provider.lang.USEnglishLanguageProvider
import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber

@EventBusSubscriber(modid = BreadMod.ID, bus = EventBusSubscriber.Bus.MOD)
object ModBusEventHandler {
    // Data Generation
    @SubscribeEvent
    fun gatherData(event: GatherDataEvent) {
        event.generator.let {
            event.includeClient().let { clientIncluded ->
                LOGGER.debug("Data generation: client")
//                it.addProvider(clientIncluded, BlockStates(it, event.existingFileHelper))
                it.addProvider(clientIncluded, ItemModels(it, event.existingFileHelper))
                it.addProvider(clientIncluded, BlockModels(it, event.existingFileHelper))
//                it.addProvider(clientIncluded, LanguageProvider(it, "en_us"))
                LOGGER.debug("Data generation: client (finished)")
            }

            event.includeClient().let { serverIncluded ->
                LOGGER.debug("Data generation: server")
//                val blockTags = BlockTags(it, event.existingFileHelper)
//                it.addProvider(serverIncluded, blockTags)
                it.addProvider(serverIncluded, Recipes(it))
//                it.addProvider(serverIncluded, ItemTags(it, blockTags, event.existingFileHelper))
                LOGGER.debug("Data generation: server (finished)")
            }
        }
        LOGGER.debug("Data generation finished")
    }
}
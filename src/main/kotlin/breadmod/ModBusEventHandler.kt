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
                it.addProvider(true, Recipes(it))
                LOGGER.debug("Data generation: server (finished)")
            }
        }

        LOGGER.debug("Data generation finished")
    }
}
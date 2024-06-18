package breadmodadvanced

import breadmod.datagen.constructLootProvider
import breadmodadvanced.ModMainAdv.LOGGER
import breadmodadvanced.datagen.ModBlockStateProviderAdv
import breadmodadvanced.registry.block.ModBlocksAdv
import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Suppress("unused")
@Mod.EventBusSubscriber(modid = ModMainAdv.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object CommonModEventBusAdv {
    @SubscribeEvent
    fun gatherData(event: GatherDataEvent) {
        val generator = event.generator
        val packOutput = generator.packOutput
        val existingFileHelper = event.existingFileHelper
//        val lookupProvider = event.lookupProvider

        if(event.includeServer()) {
            LOGGER.info("Server datagen")
            generator.addProvider(true, constructLootProvider(ModBlocksAdv.ModBlockLootAdv(), packOutput))
        }

        if(event.includeClient()) {
            LOGGER.info("Client datagen")
            generator.addProvider(true, ModBlockStateProviderAdv(packOutput, existingFileHelper))
        }
    }
}
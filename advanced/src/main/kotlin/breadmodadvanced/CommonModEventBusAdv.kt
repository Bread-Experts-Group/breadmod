package breadmodadvanced

import breadmod.ModMain
import breadmodadvanced.datagen.ModBlockStateProviderAdv
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
        val lookupProvider = event.lookupProvider

        if(event.includeClient()) {
            ModMain.LOGGER.info("Client datagen.")
            generator.addProvider(true, ModBlockStateProviderAdv(packOutput, existingFileHelper))
        }
    }
}
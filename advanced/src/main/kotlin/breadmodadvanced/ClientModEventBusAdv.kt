package breadmodadvanced

import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.ModelEvent.RegisterAdditional
import net.minecraftforge.client.model.generators.ModelProvider
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Suppress("unused")
@Mod.EventBusSubscriber(modid = ModMainAdv.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object ClientModEventBusAdv {
    @SubscribeEvent
    fun registerAdditionalModels(event: RegisterAdditional) {
        event.register(ModMainAdv.modLocation("${ModelProvider.BLOCK_FOLDER}/diesel_generator/diesel_generator_door"))
    }
}
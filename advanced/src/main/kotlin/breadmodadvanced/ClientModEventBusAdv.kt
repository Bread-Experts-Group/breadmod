package breadmodadvanced

import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.ModelEvent.RegisterAdditional
import net.minecraftforge.client.model.generators.ModelProvider
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Suppress("unused")
@Mod.EventBusSubscriber(modid = ModMainAdv.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object ClientModEventBusAdv {
    private const val DIESEL_GENERATOR_ROOT = "${ModelProvider.BLOCK_FOLDER}/diesel_generator"

    @SubscribeEvent
    fun registerAdditionalModels(event: RegisterAdditional) {
        event.register(ModMainAdv.modLocation(DIESEL_GENERATOR_ROOT))
        event.register(ModMainAdv.modLocation("$DIESEL_GENERATOR_ROOT/diesel_generator_door"))
        event.register(ModMainAdv.modLocation("$DIESEL_GENERATOR_ROOT/diesel_generator_battery_upgrade"))
        event.register(ModMainAdv.modLocation("$DIESEL_GENERATOR_ROOT/diesel_generator_charging_upgrade"))
        event.register(ModMainAdv.modLocation("$DIESEL_GENERATOR_ROOT/diesel_generator_turbo_upgrade"))
    }
}
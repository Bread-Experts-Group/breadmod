package bread.mod.breadmod.neoforge

import bread.mod.breadmod.ModMainCommon
import bread.mod.breadmod.ModMainCommon.modLocation
import net.minecraft.client.resources.model.ModelResourceLocation
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.ModelEvent
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent
import net.neoforged.neoforge.client.model.generators.ModelProvider

@Suppress("unused")
@EventBusSubscriber(modid = ModMainCommon.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
internal object ClientModEventBus {
    const val TOOL_GUN_DEF = "tool_gun"

    @SubscribeEvent
    fun registerOverlays(event: RegisterGuiLayersEvent) {
//        event.registerAboveAll(ModMainCommon.modLocation("war_overlay"), WarOverlay())
    }

    @SubscribeEvent
    fun registerAdditionalModels(event: ModelEvent.RegisterAdditional) {
        event.register(modModelLoc("${ModelProvider.ITEM_FOLDER}/$TOOL_GUN_DEF/item"))
        event.register(modModelLoc("${ModelProvider.ITEM_FOLDER}/$TOOL_GUN_DEF/coil"))
        event.register(modModelLoc("${ModelProvider.BLOCK_FOLDER}/generator_on"))
        event.register(modModelLoc("${ModelProvider.BLOCK_FOLDER}/toaster/handle"))
        event.register(modModelLoc("${ModelProvider.BLOCK_FOLDER}/creative_generator/creative_generator_star"))
        event.register(modModelLoc("${ModelProvider.BLOCK_FOLDER}/creative_generator"))
        event.register(modModelLoc("${ModelProvider.BLOCK_FOLDER}/sphere"))
        event.register(modModelLoc("${ModelProvider.ITEM_FOLDER}/$TOOL_GUN_DEF/alt/tool_gun_alt"))
    }

    private fun modModelLoc(id: String) =
        ModelResourceLocation.standalone(modLocation(id))
}
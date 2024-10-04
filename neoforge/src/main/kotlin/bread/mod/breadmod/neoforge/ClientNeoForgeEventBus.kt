package bread.mod.breadmod.neoforge

import bread.mod.breadmod.ModMainCommon
import bread.mod.breadmod.util.render.renderBuffer
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RenderLevelStageEvent

// todo register the other client stuff later
@Suppress("unused")
@EventBusSubscriber(modid = ModMainCommon.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = [Dist.CLIENT])
internal object ClientNeoForgeEventBus {
    @SubscribeEvent
    fun registerStageRender(event: RenderLevelStageEvent) {
        if (event.stage != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return
        renderBuffer.removeIf { (mutableList, renderEvent) ->
            renderEvent.invoke(
                mutableList,
                event.poseStack,
                event.camera,
                event.partialTick.realtimeDeltaTicks,
                event.levelRenderer
            )
        }
    }
}
package breadmod

import breadmod.ClientModEventBus.createMappingsForControls
import breadmod.util.render.renderBuffer
import net.minecraft.client.Minecraft
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RenderLevelStageEvent
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import org.apache.commons.lang3.ArrayUtils

@Suppress("unused")
@Mod.EventBusSubscriber(modid = ModMain.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object ClientForgeEventBus {
    /**
     * Level scene render event.
     * @see renderBuffer
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    @SubscribeEvent
    fun onLevelRender(event: RenderLevelStageEvent) {
        if (event.stage != RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) return
        renderBuffer.removeIf { it.invoke(event) }
    }

    @Suppress("UNUSED_PARAMETER")
    @SubscribeEvent
    fun levelLeave(event: PlayerLoggedOutEvent) {
        val options = Minecraft.getInstance().options
        options.keyMappings = ArrayUtils.removeElements(options.keyMappings, *createMappingsForControls().toTypedArray())
    }
}
package breadmod

import breadmod.ClientModEventBus.createMappingsForControls
import breadmod.client.render.tool_gun.ToolGunAnimationHandler
import breadmod.util.render.renderBuffer
import net.minecraft.client.Minecraft
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RenderLevelStageEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent
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
        renderBuffer.removeIf { (mutableList, renderStageEvent) -> renderStageEvent.invoke(mutableList, event) }
    }

    @Suppress("UNUSED_PARAMETER")
    @SubscribeEvent
    fun myLogin(event: PlayerLoggedInEvent) {
        val options = Minecraft.getInstance().options
        options.keyMappings = ArrayUtils.removeElements(options.keyMappings, *createMappingsForControls().toTypedArray())
    }

    @Suppress("UNUSED_PARAMETER")
    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null) return

        ToolGunAnimationHandler.tick()
    }
}
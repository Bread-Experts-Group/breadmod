package breadmod.util.render

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GameRenderer
import net.minecraftforge.client.event.RenderLevelStageEvent
import org.joml.Vector3f

/**
 * A list of lambdas to call for rendering. If lambdas return true, they will be removed.
 * @see breadmod.ClientForgeEventBus.onLevelRender
 * @author Miko Elbrecht
 * @since 1.0.0
 */
val renderBuffer = mutableListOf<(RenderLevelStageEvent) -> Boolean>()

/**
 * Draws a line from between [start] and [end], translated according to the current [net.minecraft.client.player.LocalPlayer]'s position.
 *
 * **TODO:** Implement line thickness using QUADS.
 * @see breadmod.network.BeamPacket
 * @author Miko Elbrecht
 * @since 1.0.0
 */
fun addBeamTask(start: Vector3f, end: Vector3f) = renderBuffer.add {
    val playerEyePos = (Minecraft.getInstance().player ?: return@add true).getEyePosition(it.partialTick)

    RenderSystem.enableBlend()
    RenderSystem.defaultBlendFunc()
    RenderSystem.setShader { GameRenderer.getPositionColorShader() }

    val tesselator = Tesselator.getInstance()
    it.poseStack.pushPose()
    it.poseStack.translate(-playerEyePos.x, -playerEyePos.y, -playerEyePos.z)
    val stack = it.poseStack.last().pose()
    
    val builder = tesselator.builder
    builder.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR)
    builder.vertex(stack, start.x, start.y, start.z).color(0f,1f,1f,1f).endVertex()
    builder.vertex(stack, end.x, end.y, end.z).color(0f,1f,1f,1f).endVertex()

    tesselator.end()
    RenderSystem.disableBlend()
    it.poseStack.popPose()

    return@add false
}
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
 * @see breadmod.network.BeamPacket
 * @author Miko Elbrecht
 * @since 1.0.0
 */
fun addBeamTask(start: Vector3f, end: Vector3f, thickness: Double?) = renderBuffer.add {
    val playerEyePos = (Minecraft.getInstance().player ?: return@add true).getEyePosition(it.partialTick)

    RenderSystem.enableBlend()
    RenderSystem.defaultBlendFunc()
    RenderSystem.setShader { GameRenderer.getPositionColorShader() }

    val tessellator = Tesselator.getInstance()
    it.poseStack.pushPose()
    it.poseStack.translate(-playerEyePos.x, -playerEyePos.y, -playerEyePos.z)
    val stack = it.poseStack.last().pose()
    
    val builder = tessellator.builder
    builder.begin(if(thickness != null) VertexFormat.Mode.QUADS else VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR)
    // Q1
    builder.vertex(stack, start.x, start.y, start.z).color(0f,1f,1f,1f).endVertex()
    builder.vertex(stack, end.x, end.y, end.z).color(0f,1f,1f,1f).endVertex()
    if(thickness != null) {
        builder.vertex(stack, start.x + 0.1f, start.y, start.z).color(0f,1f,1f,1f).endVertex()
        builder.vertex(stack, end.x + 0.1f, end.y, end.z).color(0f,1f,1f,1f).endVertex()
        // Q2
        builder.vertex(stack, start.x, start.y + 0.1f, start.z).color(0f,1f,1f,1f).endVertex()
        builder.vertex(stack, end.x, end.y + 0.1f, end.z).color(0f,1f,1f,1f) .endVertex()

        builder.vertex(stack, start.x + 0.1f, start.y + 0.1f, start.z).color(0f,1f,1f,1f).endVertex()
        builder.vertex(stack, end.x + 0.1f, end.y + 0.1f, end.z).color(0f,1f,1f,1f).endVertex()
        // Q3
        builder.vertex(stack, start.x + 0.1f, start.y, start.z).color(0f,1f,1f,1f).endVertex()
        builder.vertex(stack, end.x + 0.1f, end.y, end.z).color(0f,1f,1f,1f) .endVertex()

        builder.vertex(stack, start.x + 0.1f, start.y + 0.1f, start.z).color(0f,1f,1f,1f).endVertex()
        builder.vertex(stack, end.x + 0.1f, end.y + 0.1f, end.z).color(0f,1f,1f,1f).endVertex()
        // Q4
        builder.vertex(stack, start.x, start.y, start.z).color(0f,1f,1f,1f).endVertex()
        builder.vertex(stack, end.x, end.y, end.z).color(0f,1f,1f,1f) .endVertex()

        builder.vertex(stack, start.x, start.y + 0.1f, start.z).color(0f,1f,1f,1f).endVertex()
        builder.vertex(stack, end.x, end.y + 0.1f, end.z).color(0f,1f,1f,1f).endVertex()
    }

    tessellator.end()
    RenderSystem.disableBlend()
    it.poseStack.popPose()

    return@add false
}
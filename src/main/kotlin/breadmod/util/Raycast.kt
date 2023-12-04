package breadmod.util

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.math.Matrix4f
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.DebugScreenOverlay
import org.lwjgl.opengl.GL11
import java.awt.Color

fun entityRaycast(entity: Entity, maxDistance: Boolean, includeFluids: Boolean) {
    val directionVector = Vec3.directionFromRotation(entity.rotationVector)
}

@SubscribeEvent
fun renderLine(event) {
    DebugScreenOverlay
    val view = Minecraft.getInstance().gameRenderer.mainCamera.position

    RenderSystem.depthMask(false)
    RenderSystem.disableCull()
    RenderSystem.enableBlend()
    RenderSystem.defaultBlendFunc()
    RenderSystem.disableTexture()
    GL11.glEnable(GL11.GL_LINE_SMOOTH)
    GL11.glDisable(GL11.GL_DEPTH_TEST)

    val tesselator = Tesselator.getInstance()
    val buffer = tesselator.builder
    buffer.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR)
    buffer.vertex(0.0, 0.0, 0.0).color(1f, 1f, 1f, 1f).endVertex()
    buffer.vertex(100.0, 200.0, 100.0).color(1f, 1f, 1f, 1f).endVertex()

    vertexBuffer.bind()
    vertexBuffer.upload(buffer.end())

    PoseStack matrix = event.getPoseStack()
    matrix.pushPose()
    matrix.translate(-view.x, -view.y, -view.z)
    var shader = GameRenderer.getPositionColorShader()
    vertexBuffer.drawWithShader(matrix.last().pose(), event.getProjectionMatrix().copy(), shader)
    matrix.popPose()

    VertexBuffer.unbind()
}

private fun drawLine(matrix: Matrix4f, buffer: BufferBuilder, p1: Vec3d, p2: Vec3d, color: Color4f) {
    buffer.pos(matrix, p1.x as Float + 0.5f, p1.y as Float + 1.0f, p1.z as Float + 0.5f)
        .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
        .endVertex()
    buffer.pos(matrix, p2.x as Float + 0.5f, p2.y as Float, p2.z as Float + 0.5f)
        .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
        .endVertex()
}
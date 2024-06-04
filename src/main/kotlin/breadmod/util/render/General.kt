package breadmod.util.render

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.world.phys.Vec3

val renderBuffer = mutableListOf<() -> Unit>()
fun drawLine(start: Vec3, end: Vec3, thickness: Float) {
    val bufferBuilder = Tesselator.getInstance().builder

    val direction = end.subtract(start).normalize()
    val perpendicular = direction.cross(Vec3(0.0, 1.0, 0.0)).normalize().scale(thickness / 2.0)
    val p1 = start.add(perpendicular)
    val p2 = start.subtract(perpendicular)
    val p3 = end.add(perpendicular)
    val p4 = end.subtract(perpendicular)

    renderBuffer.add {
        RenderSystem.setShader { GameRenderer.getPositionColorShader() }
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.lineWidth(thickness)

        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR)

        bufferBuilder.vertex(p1.x, p1.y, p1.z).color(255, 255, 255, 255).endVertex()
        bufferBuilder.vertex(p2.x, p2.y, p2.z).color(255, 255, 255, 255).endVertex()
        bufferBuilder.vertex(p4.x, p4.y, p4.z).color(255, 255, 255, 255).endVertex()
        bufferBuilder.vertex(p3.x, p3.y, p3.z).color(255, 255, 255, 255).endVertex()

        Tesselator.getInstance().end()
        RenderSystem.disableBlend()
    }
}
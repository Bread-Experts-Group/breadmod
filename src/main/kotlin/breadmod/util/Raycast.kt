package breadmod.util

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult
import net.minecraftforge.fluids.IFluidBlock

fun Entity.raycast(maxDistance: Int, includeFluids: Boolean): HitResult {
    val directionVector = Vec3.directionFromRotation(this.rotationVector)

    var lastVec3 = Vec3.ZERO
    var lastBlockPos = BlockPos.ZERO

    repeat(maxDistance) {
        lastVec3 = this.eyePosition.add(directionVector.scale(it.toDouble()))
        lastBlockPos = BlockPos(lastVec3)

        val block = this.level.getBlockState(lastBlockPos)
        if((block is IFluidBlock && includeFluids) || !block.isAir)
            return BlockHitResult(lastVec3, direction, lastBlockPos, true)

        val foundEntity = this.level.getEntities(this, AABB.ofSize(lastVec3, 1.0, 1.0, 1.0)).firstOrNull()
        if(foundEntity != null) return EntityHitResult(foundEntity, lastVec3)
    }

    return BlockHitResult.miss(lastVec3, direction, lastBlockPos)
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
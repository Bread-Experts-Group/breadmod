package org.bread_experts_group.breadmod.experimental.camera

import com.mojang.blaze3d.pipeline.RenderTarget
import com.mojang.blaze3d.platform.NativeImage
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.world.entity.player.Player
import org.bread_experts_group.breadmod.util.render.initialTranslate
import org.bread_experts_group.breadmod.util.render.renderBuffer
import org.bread_experts_group.breadmod.util.render.localClient

fun testCameraView(player: Player) {
    val x = player.x
    val y = player.y
    val z = player.z

    renderBuffer.add(mutableListOf(1f) to { mutableList, renderStageEvent ->
        val kill = mutableList[0]
        val poseStack = renderStageEvent.poseStack
        poseStack.pushPose()
        poseStack.initialTranslate(renderStageEvent.camera)
        poseStack.translate(x, y, z)
        val renderTarget = Minecraft.getInstance().mainRenderTarget
        val image = screenshot(renderTarget)
        val texture = DynamicTexture(image)
        val resource = localClient.textureManager.register("test", texture)
        poseStack.mulPose(Axis.XN.rotationDegrees(90f))

        RenderSystem.setShaderTexture(0, resource)
        RenderSystem.setShader(GameRenderer::getPositionTexShader)
        val pose = poseStack.last().pose()
        val bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX)
        bufferBuilder.addVertex(pose, 0f, 0f, 0f).setUv(1f, 1f)
        bufferBuilder.addVertex(pose, 0f, 0f, 2f).setUv(1f, 0f)
        bufferBuilder.addVertex(pose, 3f, 0f, 2f).setUv(0f, 0f)
        bufferBuilder.addVertex(pose, 3f, 0f, 0f).setUv(0f, 1f)
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow())
        localClient.textureManager.release(resource)
        image.close()

        renderStageEvent.poseStack.popPose()
        mutableList[0] = kill - 0.005f
        if (mutableList[0] <= 0f) true else false
    })
}

fun screenshot(
    frameBuffer: RenderTarget,
    width: Int = frameBuffer.width,
    height: Int = frameBuffer.height
): NativeImage {
    val nativeImage = NativeImage(width, height, false)
    RenderSystem.bindTexture(frameBuffer.colorTextureId)
    nativeImage.downloadTexture(0, true)
    nativeImage.flipY()
    return nativeImage
}
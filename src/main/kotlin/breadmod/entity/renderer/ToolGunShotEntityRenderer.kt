package breadmod.entity.renderer

import breadmod.ModMain
import breadmod.entity.ToolGunShotEntity
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation

class ToolGunShotEntityRenderer(pContext: EntityRendererProvider.Context) : EntityRenderer<ToolGunShotEntity>(pContext) { // todo replace with custom renderer (that's just a small box or point)
    override fun getTextureLocation(pEntity: ToolGunShotEntity): ResourceLocation =
        ModMain.modLocation("textures","entity","projectile","bread_bullet.png")

    override fun render(
        pEntity: ToolGunShotEntity,
        pEntityYaw: Float,
        pPartialTick: Float,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int
    ) {
        pPoseStack.pushPose()
        pPoseStack.mulPose(this.entityRenderDispatcher.cameraOrientation())
    }
}
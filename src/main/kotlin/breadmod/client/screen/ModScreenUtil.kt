package breadmod.client.screen

import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.entity.LivingEntity
import org.joml.Matrix4f
import org.joml.Quaternionf
import kotlin.math.atan

fun renderEntityInInventoryFollowsMouse(
    pGuiGraphics: GuiGraphics,
    pX: Int,
    pY: Int,
    pScale: Int,
    pMouseX: Float,
    pMouseY: Float,
    pEntity: LivingEntity
) {
    val f = atan((pMouseX / 40.0f).toDouble()).toFloat()
    val f1 = atan((pMouseY / 40.0f).toDouble()).toFloat()
    // Forge: Allow passing in direct angle components instead of mouse position
    renderEntityInInventoryFollowsAngle(pGuiGraphics, pX, pY, pScale, f, f1, pEntity)
}

fun renderEntityInInventoryFollowsAngle(
    pGuiGraphics: GuiGraphics,
    pX: Int,
    pY: Int,
    pScale: Int,
    angleXComponent: Float,
    angleYComponent: Float,
    pEntity: LivingEntity
) {
    val quaternionF = Quaternionf().rotateZ(Math.PI.toFloat())
    val quaternionF1 = Quaternionf().rotateX(angleYComponent * 20.0f * (Math.PI.toFloat() / 180f))
    quaternionF.mul(quaternionF1)
    val f2 = pEntity.yBodyRot
    val f3 = pEntity.yRot
    val f4 = pEntity.xRot
    val f5 = pEntity.yHeadRotO
    val f6 = pEntity.yHeadRot
    pEntity.yBodyRot = 180.0f + angleXComponent * 20.0f
    pEntity.yRot = 180.0f + angleXComponent * 40.0f
    pEntity.xRot = -angleYComponent * 20.0f
    pEntity.yHeadRot = pEntity.yRot
    pEntity.yHeadRotO = pEntity.yRot
    renderEntityInInventory(pGuiGraphics, pX, pY, pScale, quaternionF, quaternionF1, pEntity)
    pEntity.yBodyRot = f2
    pEntity.yRot = f3
    pEntity.xRot = f4
    pEntity.yHeadRotO = f5
    pEntity.yHeadRot = f6
}

fun renderEntityInInventory(
    pGuiGraphics: GuiGraphics,
    pX: Int,
    pY: Int,
    pScale: Int,
    pPose: Quaternionf,
    pCameraOrientation: Quaternionf?,
    pEntity: LivingEntity
) {
    pGuiGraphics.pose().pushPose()
    pGuiGraphics.pose().translate(pX.toDouble(), pY.toDouble(), 50.0)
    pGuiGraphics.pose().mulPoseMatrix(Matrix4f().scaling(pScale.toFloat(), pScale.toFloat(), (-pScale).toFloat()))
    pGuiGraphics.pose().mulPose(pPose)
    Lighting.setupForEntityInInventory()
    val entityRenderDispatcher = Minecraft.getInstance().entityRenderDispatcher
    if (pCameraOrientation != null) {
        pCameraOrientation.conjugate()
        entityRenderDispatcher.overrideCameraOrientation(pCameraOrientation)
    }

    entityRenderDispatcher.setRenderShadow(false)
    RenderSystem.runAsFancy {
        entityRenderDispatcher.render(
            pEntity,
            0.0,
            0.0,
            0.0,
            0.0f,
            1.0f,
            pGuiGraphics.pose(),
            pGuiGraphics.bufferSource(),
            15728880
        )
    }
    pGuiGraphics.flush()
    entityRenderDispatcher.setRenderShadow(true)
    pGuiGraphics.pose().popPose()
    Lighting.setupFor3DItems()
}
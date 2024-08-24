package breadmod.client.render.tool_gun

import breadmod.ModMain.modLocation
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.TOOL_GUN_DEF
import breadmod.item.tool_gun.ToolGunItem
import breadmod.registry.ModConfiguration.COMMON
import breadmod.util.render.renderItemModel
import breadmod.util.render.rgMinecraft
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.ChatFormatting
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraftforge.client.model.generators.ModelProvider
import java.awt.Color
import java.security.SecureRandom
import kotlin.math.round


class ToolGunItemRenderer : BlockEntityWithoutLevelRenderer(
    rgMinecraft.blockEntityRenderDispatcher,
    rgMinecraft.entityModels
) {
    private companion object {
        val secureRandom = SecureRandom()
    }

    private val mainModelLocation = modLocation("${ModelProvider.ITEM_FOLDER}/$TOOL_GUN_DEF/item")
    private val coilModelLocation = modLocation("${ModelProvider.ITEM_FOLDER}/$TOOL_GUN_DEF/coil")
    private val altModelLocation = modLocation("${ModelProvider.ITEM_FOLDER}/$TOOL_GUN_DEF/alt/tool_gun_alt")
    private val useAltModel = COMMON.ALT_TOOLGUN_MODEL
//    private val testModelLocation = modLocation("${ModelProvider.ITEM_FOLDER}/textureplane/textureplane_test")

    override fun renderByItem(
        pStack: ItemStack,
        pDisplayContext: ItemDisplayContext,
        pPoseStack: PoseStack,
        pBuffer: MultiBufferSource,
        pPackedLight: Int,
        pPackedOverlay: Int
    ) {
        val toolGunItem = pStack.item as ToolGunItem
        val toolGunMode = toolGunItem.getCurrentMode(pStack)
        val renderer = rgMinecraft.itemRenderer
        val fontRenderer = rgMinecraft.font
        val modelManager = rgMinecraft.modelManager
        val mainModel = modelManager.getModel(mainModelLocation)
        val coilModel = modelManager.getModel(coilModelLocation)
        val altModel = modelManager.getModel(altModelLocation)

        val animHandler = ToolGunAnimationHandler
        val rotation = animHandler.coilRotation
        val recoil = animHandler.recoil

//        pPoseStack.pushPose()
//      // todo recoil
        // todo import the model from gmod

        animHandler.clientTick()

        if(pDisplayContext.firstPerson()) {
            pPoseStack.translate(-recoil, 0.0f, 0.0f)
            if (useAltModel.get()) {
                renderItemModel(altModel, renderer, pStack, pPoseStack, pBuffer, pPackedOverlay, pPackedLight)
            } else {
                renderItemModel(mainModel, renderer, pStack, pPoseStack, pBuffer, pPackedOverlay, pPackedLight)

                drawTextOnScreen(
                    (toolGunItem.getCurrentMode(pStack).displayName.copy()).withStyle(ChatFormatting.BOLD),
                    Color.WHITE.rgb, Color(0,0,0,0).rgb, false, fontRenderer, pPoseStack, pBuffer,
                    0.923, 0.065, -0.038, 0.0007f
                )

                drawTextOnScreen("CASEOH: ${round(secureRandom.nextDouble() * 5000).toUInt()}lbs", Color.RED.rgb, Color(0,0,0,0).rgb, false, fontRenderer, pPoseStack, pBuffer,
                    0.9, 0.0175, -0.040, 0.0007f
                )

                toolGunMode.mode.render(pStack, pDisplayContext, pPoseStack, pBuffer, pPackedLight, pPackedOverlay)

                pPoseStack.mulPose(Axis.XN.rotationDegrees(rotation))
                renderItemModel(coilModel, renderer, pStack, pPoseStack, pBuffer, pPackedOverlay, pPackedLight)
            }
        } else {
            if (useAltModel.get()) {
                renderItemModel(altModel, renderer, pStack, pPoseStack, pBuffer, pPackedOverlay, pPackedLight)
            } else {
                renderItemModel(mainModel, renderer, pStack, pPoseStack, pBuffer, pPackedOverlay, pPackedLight)
                pPoseStack.mulPose(Axis.XN.rotationDegrees(rotation))
                renderItemModel(coilModel, renderer, pStack, pPoseStack, pBuffer, pPackedOverlay, pPackedLight)
            }
        }
//        pPoseStack.popPose()

        // x, y, z after rotations
        // x: back and forward, y: up and down, z: left and right

        // Tool gun mode specific rendering
//        val byteArray = ByteArray(2)
//        secureRandom.nextBytes(byteArray)
//        drawTextOnScreen(byteArray.decodeToString(), Color.BLACK.rgb, Color(25,25,25,0).rgb, fontRenderer, pPoseStack, pBuffer,
//            0.9215, 0.0555, -0.028, 0.0035f
//        )
    }
}

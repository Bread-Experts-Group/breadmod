package bread.mod.breadmod.client.render

import bread.mod.breadmod.ModMainCommon.modLocation
import bread.mod.breadmod.command.client.AltToolGunModelCommand.useAltModel
import bread.mod.breadmod.item.toolGun.ToolGunAnimationHandler
import bread.mod.breadmod.item.toolGun.ToolGunItem.Companion.TOOL_GUN_DEF
import bread.mod.breadmod.item.toolGun.drawTextOnScreen
import bread.mod.breadmod.util.render.renderItemModel
import bread.mod.breadmod.util.render.rgMinecraft
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.architectury.platform.Platform
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
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

    private val platformId = if (Platform.isFabric()) "fabric_resource" else "standalone"

    private val mainModelLocation = ModelResourceLocation(modLocation("item/$TOOL_GUN_DEF/item"), platformId)
    private val coilModelLocation = ModelResourceLocation(modLocation("item/$TOOL_GUN_DEF/coil"), platformId)
    private val altModelLocation = ModelResourceLocation(modLocation("item/$TOOL_GUN_DEF/alt/tool_gun_alt"), platformId)

    //    private val useAltModel = CLIENT.ALT_TOOLGUN_MODEL
    override fun renderByItem(
        stack: ItemStack,
        displayContext: ItemDisplayContext,
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
//    val toolGunItem = stack.item as ToolGunItem
//      val toolGunMode = toolGunItem.getCurrentMode(stack)
        val renderer = rgMinecraft.itemRenderer
        val fontRenderer = rgMinecraft.font
        val modelManager = rgMinecraft.modelManager
        val mainModel = modelManager.getModel(mainModelLocation)
        val coilModel = modelManager.getModel(coilModelLocation)
        val altModel = modelManager.getModel(altModelLocation)

        val animHandler = ToolGunAnimationHandler
        val rotation = animHandler.coilRotation
        val recoil = animHandler.recoil

        fun rotateCoilAndRender() {
            poseStack.mulPose(Axis.XN.rotationDegrees(rotation))
            renderer.renderItemModel(
                coilModel,
                stack,
                displayContext,
                false,
                poseStack,
                buffer,
                packedOverlay,
                packedLight
            )
        }

        // todo recoil

        animHandler.clientTick()

        if (displayContext.firstPerson()) {
            poseStack.translate(-recoil, 0.0f, 0.0f)
            if (useAltModel) {
//                renderItemModel(altModel, renderer, stack, poseStack, buffer, packedOverlay, packedLight)
                renderer.renderItemModel(
                    altModel,
                    stack,
                    displayContext,
                    false,
                    poseStack,
                    buffer,
                    packedOverlay,
                    packedLight
                )
            } else {
//                renderModel(mainModel, renderer, stack, poseStack, buffer, packedOverlay, packedLight)
                renderer.renderItemModel(
                    mainModel,
                    stack,
                    displayContext,
                    false,
                    poseStack,
                    buffer,
                    packedOverlay,
                    packedLight
                )
//            renderer.render(stack, displayContext, false, poseStack, buffer, packedLight, packedOverlay, mainModel)
//            renderer.render(stack, displayContext, false, poseStack, buffer, packedLight, packedOverlay, mainModel)

//                drawTextOnScreen(
//                    (toolGunItem.getCurrentMode(stack).displayName.copy()).withStyle(ChatFormatting.BOLD),
//                    Color.WHITE.rgb, Color(0, 0, 0, 0).rgb, false, fontRenderer, poseStack, buffer,
//                    0.923, 0.065, -0.038, 0.0007f
//                )
                drawTextOnScreen(
                    Component.literal("THE FUNNY"),
                    Color.WHITE.rgb, Color(0, 0, 0, 0).rgb, false, fontRenderer, poseStack, buffer,
                    0.923, 0.065, -0.038, 0.0007f
                )

                drawTextOnScreen(
                    "CASEOH: ${round(secureRandom.nextDouble() * 5000).toUInt()}lbs",
                    Color.RED.rgb,
                    Color(0, 0, 0, 0).rgb,
                    false,
                    fontRenderer,
                    poseStack,
                    buffer,
                    0.9,
                    0.0175,
                    -0.040,
                    0.0007f
                )

//                toolGunMode.mode.render(stack, displayContext, poseStack, buffer, packedLight, packedOverlay)

                rotateCoilAndRender()
//            renderer.render(stack, displayContext, false, poseStack, buffer, packedLight, packedOverlay, coilModel)
            }
        } else {
            if (useAltModel) {
                renderer.renderItemModel(
                    altModel,
                    stack,
                    displayContext,
                    false,
                    poseStack,
                    buffer,
                    packedOverlay,
                    packedLight
                )
            } else {
                renderer.renderItemModel(
                    mainModel,
                    stack,
                    displayContext,
                    false,
                    poseStack,
                    buffer,
                    packedOverlay,
                    packedLight
                )
                rotateCoilAndRender()
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
package bread.mod.breadmod.client.render.item

import bread.mod.breadmod.item.toolGun.ToolGunAnimationHandler
import bread.mod.breadmod.item.toolGun.ToolGunItem.Companion.TOOL_GUN_DEF
import bread.mod.breadmod.item.toolGun.drawTextOnScreen
import bread.mod.breadmod.registry.config.ClientConfig
import bread.mod.breadmod.util.render.*
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.Util
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Blocks
import java.awt.Color
import java.security.SecureRandom
import kotlin.math.round

class ToolGunItemRenderer :
    BlockEntityWithoutLevelRenderer(rgMinecraft.blockEntityRenderDispatcher, rgMinecraft.entityModels) {
    private companion object {
        val secureRandom = SecureRandom()
    }

    private val mainModelLocation = modelLocation("item/$TOOL_GUN_DEF/item")
    private val coilModelLocation = modelLocation("item/$TOOL_GUN_DEF/coil")
    private val altModelLocation = modelLocation("item/$TOOL_GUN_DEF/alt/tool_gun_alt")
    private val useAltModel = ClientConfig.ALT_TOOLGUN_MODEL

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
        val modelManager = rgMinecraft.modelManager
        val itemRenderer = rgMinecraft.itemRenderer
        val blockModelRenderer = rgMinecraft.blockRenderer.modelRenderer
        val font = rgMinecraft.font

        val mainModel = modelManager.getModel(mainModelLocation)
        val coilModel = modelManager.getModel(coilModelLocation)
        val altModel = modelManager.getModel(altModelLocation)

        val animHandler = ToolGunAnimationHandler
        val rotation = animHandler.coilRotation
        val recoil = animHandler.recoil

        fun rotateCoilAndRender() {
            poseStack.mulPose(Axis.XN.rotationDegrees(rotation))
            itemRenderer.renderItemModel(
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

        val millis = Util.getMillis()

        poseStack.pushPose()
        poseStack.translate(1.1 - recoil, 0.06, -0.05)
        poseStack.scaleFlat(0.08f)
        poseStack.translate(0.5, 0.0, 0.5)
        poseStack.mulPose(Axis.YP.rotationDegrees((millis.toFloat() / 50f) % 360f))
        poseStack.translate(-0.5, 0.0, -0.5)
        blockModelRenderer.renderBlockModel(
            poseStack.last(),
            buffer,
            Blocks.HAY_BLOCK.defaultBlockState(),
            packedLight,
            packedOverlay
        )
        poseStack.popPose()

        // todo proper recoil

        animHandler.clientTick()

        if (displayContext.firstPerson()) {
            poseStack.translate(-recoil, 0.0f, 0.0f)
            if (useAltModel.valueOrThrow()) {
                itemRenderer.renderItemModel(
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
                itemRenderer.renderItemModel(
                    mainModel,
                    stack,
                    displayContext,
                    false,
                    poseStack,
                    buffer,
                    packedOverlay,
                    packedLight
                )

//                drawTextOnScreen(
//                    (toolGunItem.getCurrentMode(stack).displayName.copy()).withStyle(ChatFormatting.BOLD),
//                    Color.WHITE.rgb, Color(0, 0, 0, 0).rgb, false, fontRenderer, poseStack, buffer,
//                    0.923, 0.065, -0.038, 0.0007f
//                )
                drawTextOnScreen(
                    Component.literal("THE FUNNY"),
                    Color.WHITE.rgb, Color(0, 0, 0, 0).rgb, false, font, poseStack, buffer,
                    0.923, 0.065, -0.038, 0.0007f
                )

                drawTextOnScreen(
                    "CASEOH: ${round(secureRandom.nextDouble() * 5000).toUInt()}lbs",
                    Color.RED.rgb,
                    Color(0, 0, 0, 0).rgb,
                    false,
                    font,
                    poseStack,
                    buffer,
                    0.9,
                    0.0175,
                    -0.040,
                    0.0007f
                )

//                toolGunMode.mode.render(stack, displayContext, poseStack, buffer, packedLight, packedOverlay)

                rotateCoilAndRender()
            }
        } else {
            if (useAltModel.valueOrThrow()) {
                itemRenderer.renderItemModel(
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
                itemRenderer.renderItemModel(
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
package breadmod.item.tool_gun.render

import breadmod.ModMain.modLocation
import breadmod.datagen.tool_gun.BreadModToolGunModeProvider.Companion.TOOL_GUN_DEF
import breadmod.item.tool_gun.ToolGunItem
import breadmod.util.render.renderItemModel
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.Timer
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraftforge.client.model.generators.ModelProvider
import java.awt.Color
import java.security.SecureRandom
import kotlin.math.round


class ToolGunItemRenderer : BlockEntityWithoutLevelRenderer(
    minecraft.blockEntityRenderDispatcher,
    minecraft.entityModels
) {
    private companion object {
        val minecraft: Minecraft = Minecraft.getInstance()
        val secureRandom = SecureRandom()
        private val timer = Timer(20f, 0).partialTick // todo replace with what create uses since this has odd behaviour
        private var angle = 0f
    }

    private val mainModelLocation = modLocation("${ModelProvider.ITEM_FOLDER}/$TOOL_GUN_DEF/item")
    private val coilModelLocation = modLocation("${ModelProvider.ITEM_FOLDER}/$TOOL_GUN_DEF/coil")
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

        val instance = Minecraft.getInstance()
        val renderer = instance.itemRenderer
        val fontRenderer = instance.font
        val modelManager = instance.modelManager
        val mainModel = modelManager.getModel(mainModelLocation)
        val coilModel = modelManager.getModel(coilModelLocation)
//        val testModel = modelManager.getModel(testModelLocation)

        val f: Float = 1f + timer
        if(!instance.isPaused) angle += f; angle %= 360
        println(angle)

        pPoseStack.pushPose()
        // todo smooth rotation after firing toolgun, quickly tapering off. GameRenderer.java@1177 found a function that uses the same posestack calls as this, could be used for smooth anims
//        // todo recoil and increased coil spin when using tool gun

//        pPoseStack.translate(sin(angle.toDouble() / 30), 0.0, 0.0)
        renderItemModel(mainModel, renderer, pStack, pPoseStack, pBuffer, pPackedOverlay, pPackedLight)
        pPoseStack.mulPose(Axis.XN.rotationDegrees(angle))
        renderItemModel(coilModel, renderer, pStack, pPoseStack, pBuffer, pPackedOverlay, pPackedLight)
//        pPoseStack.mulPose(Axis.YN.rotationDegrees(angle * 30))
//        renderModel(testModel, renderer, pStack, pPoseStack, pBuffer, pPackedOverlay, pPackedLight)
        pPoseStack.popPose()
        // x, y, z after rotations
        // x: back and forward, y: up and down, z: left and right

        // Tool gun mode specific rendering
//        val byteArray = ByteArray(2)
//        secureRandom.nextBytes(byteArray)
//        drawTextOnScreen(byteArray.decodeToString(), Color.BLACK.rgb, Color(25,25,25,0).rgb, fontRenderer, pPoseStack, pBuffer,
//            0.9215, 0.0555, -0.028, 0.0035f
//        )

        drawTextOnScreen(
            (toolGunItem.getCurrentMode(pStack).displayName.copy()).withStyle(ChatFormatting.BOLD),
            Color.WHITE.rgb, Color(0,0,0,0).rgb, false, fontRenderer, pPoseStack, pBuffer,
            0.923, 0.065, -0.038, 0.0007f
        )

        drawTextOnScreen("CASEOH: ${round(secureRandom.nextDouble() * 5000).toUInt()}lbs", Color.RED.rgb, Color(0,0,0,0).rgb, false, fontRenderer, pPoseStack, pBuffer,
            0.9, 0.0175, -0.040, 0.0007f
        )

        pPoseStack.pushPose()
        toolGunMode.mode.render(pStack, pPoseStack, pBuffer, pPackedLight, pPackedOverlay)
        pPoseStack.popPose()
    }
}

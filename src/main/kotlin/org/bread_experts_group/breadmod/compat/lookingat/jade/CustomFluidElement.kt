package org.bread_experts_group.breadmod.compat.lookingat.jade

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.phys.Vec2
import org.bread_experts_group.breadmod.util.render.localClient
import snownee.jade.api.fluid.JadeFluidObject
import snownee.jade.api.ui.Element
import snownee.jade.overlay.DisplayHelper
import java.awt.Color

class CustomFluidElement(val fluid: JadeFluidObject, val capacity: Int) : Element() {
    override fun getSize(): Vec2 = Vec2(14f, 14f)

    override fun render(guiGraphics: GuiGraphics, x: Float, y: Float, maxX: Float, maxY: Float) {
        val text = Component.translatable(fluid.type.fluidType.descriptionId).append(": ${fluid.amount}mB")
        val poseStack = guiGraphics.pose()
        poseStack.pushPose()
        DisplayHelper.fill(guiGraphics, x, y, x + 100, y + 1, Color.ORANGE.rgb)
        DisplayHelper.fill(guiGraphics, x, y, x + 1, y + 14, Color.ORANGE.rgb)
        DisplayHelper.fill(guiGraphics, x + 99, y, x + 100, y + 14, Color.ORANGE.rgb)
        DisplayHelper.fill(guiGraphics, x, y + 13, x + 100, y + 14, Color.ORANGE.rgb)
        DisplayHelper.INSTANCE.drawFluid(
            guiGraphics,
            x + 1,
            y + 1,
            fluid,
            ((fluid.amount.toFloat() / capacity) * 98),
            12f,
            JadeFluidObject.bucketVolume()
        )
        guiGraphics.drawString(
            localClient.font,
            text.visualOrderText,
            x + 2,
            y + 3,
            Color.WHITE.rgb,
            true
        )
        poseStack.popPose()
    }
}
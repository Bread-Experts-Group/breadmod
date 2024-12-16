package org.bread_experts_group.breadmod.compat.lookingat.jade

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.world.phys.Vec2
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.util.render.localClient
import snownee.jade.api.fluid.JadeFluidObject
import snownee.jade.api.ui.Element
import snownee.jade.overlay.DisplayHelper
import java.awt.Color

class CustomFluidElement(val fluid: JadeFluidObject, val capacity: Int, val direction: Direction?) : Element() {
    override fun getSize(): Vec2 = Vec2(140f, 14f)

    private val uV = mapOf(
        *listOf(
            null,
            Direction.DOWN,
            Direction.UP,
            Direction.NORTH,
            Direction.WEST,
            Direction.SOUTH,
            Direction.EAST,
        ).mapIndexed { index, direction -> direction to index * 16 }.toTypedArray()
    )

    override fun render(guiGraphics: GuiGraphics, x: Float, y: Float, maxX: Float, maxY: Float) {
        val text =
            Component.translatable(if (!fluid.isEmpty) fluid.type.fluidType.descriptionId else "tooltip.jade.empty")
        val poseStack = guiGraphics.pose()
        poseStack.pushPose()
        DisplayHelper.INSTANCE.drawBorder(
            guiGraphics,
            x, y,
            x + 100, y + 14,
            1f,
            Color.ORANGE.rgb,
            false
        )
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
        val amount = "${fluid.amount}mB"
        guiGraphics.drawString(
            localClient.font,
            amount,
            x + size.x - localClient.font.width(amount) - 42,
            y + 3,
            Color.GRAY.rgb,
            true
        )
        RenderSystem.enableBlend()
        guiGraphics.blit(
            modLocation("textures", "gui", "cube_sprites.png"),
            x.toInt() + 102,
            y.toInt() - 1,
            uV[direction]!!, 0, 16, 16
        )
        RenderSystem.disableBlend()
        poseStack.popPose()
    }
}
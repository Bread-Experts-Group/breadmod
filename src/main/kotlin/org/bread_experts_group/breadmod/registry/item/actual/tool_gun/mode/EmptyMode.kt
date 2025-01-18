package org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.api.ToolGunMode
import org.bread_experts_group.breadmod.client.render.tool_gun.ToolGunRenderContext
import org.bread_experts_group.breadmod.client.render.tool_gun.drawTextOnScreen
import org.bread_experts_group.breadmod.client.render.transparentColor
import org.bread_experts_group.breadmod.client.tool_gun_mode.ModeWidget
import java.awt.Color

@ToolGunMode
class EmptyMode : AbstractToolGunMode() {
	override fun action(level: Level, player: Player, stack: ItemStack) {
		player.sendSystemMessage(Component.literal("If you see this then the current mode is EmptyMode."))
	}

	override fun getModeWidget(): ModeWidget = ModeWidget.Builder().id(this.getUid()).build()

	override fun getDisplayName(): Component = Component.literal("???")

	override fun getTooltip(): Component =
		Component.literal("If you see this mode then something probably went wrong!")

	override fun getUid(): ResourceLocation = modLocation("tool_gun", "empty_mode")

	override fun renderScreenStage(
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		poseStack: PoseStack,
		buffer: MultiBufferSource,
		packedLight: Int,
		packedOverlay: Int,
		context: ToolGunRenderContext
	) {
		drawTextOnScreen(
			this.getDisplayName(),
			Color.WHITE.rgb, transparentColor().rgb,
			false,
			context.font,
			poseStack,
			buffer,
			posX = -0.035,
			posY = 0.414
		)
	}
}
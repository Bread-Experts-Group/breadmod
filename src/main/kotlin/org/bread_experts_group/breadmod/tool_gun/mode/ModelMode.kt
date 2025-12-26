package org.bread_experts_group.breadmod.tool_gun.mode

import com.mojang.blaze3d.platform.InputConstants
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.api.IToolGunModeRenderer
import org.bread_experts_group.breadmod.api.ToolGunMode
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.data_holders.common.KeyData
import org.bread_experts_group.breadmod.data_holders.common.ToolGunData
import org.bread_experts_group.breadmod.tool_gun.gui.screen.ModelScreen

@ToolGunMode
class ModelMode : IToolGunMode {
	override fun action(
		level: Level,
		player: Player,
		stack: ItemStack,
		usedHand: InteractionHand,
		data: ToolGunData
	) {
	}

	override fun registerKeys(into: MutableMap<Int, KeyData>) {
		into[InputConstants.KEY_F] = KeyData(Component.literal("open editor")) { event, stack, player, data ->
			if (localClient.screen == null) localClient.setScreen(ModelScreen(player))
		}
	}

	override fun getUid(): ResourceLocation = this.toolGunLocation("model_mode")

	override fun defineCustomRenderer(): IToolGunModeRenderer = ModelRenderer(this)

	class ModelRenderer(private val mode: ModelMode) : IToolGunModeRenderer {
		override fun getMode(): ModelMode = this.mode

		// todo rendering the in-progress / finished model on the tool gun, and it spins too
		override fun render(
			stack: ItemStack,
			displayContext: ItemDisplayContext,
			poseStack: PoseStack,
			buffer: MultiBufferSource,
			packedLight: Int,
			packedOverlay: Int
		) {
			ModelScreen.renderBlocks(poseStack, buffer, ModelScreen.blocks)
			ModelScreen.renderFloor(poseStack, buffer)
		}
	}
}
package org.bread_experts_group.breadmod.tool_gun

import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.core.BlockPos.MutableBlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.api.IToolGunModeRenderer
import org.bread_experts_group.breadmod.api.ToolGunMode
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.data_holders.common.ToolGunData
import org.bread_experts_group.breadmod.util.Color
import org.bread_experts_group.breadmod.util.blocks
import org.bread_experts_group.breadmod.util.rayCast

@ToolGunMode
@Suppress("unused")
class RayCastTestMode : IToolGunMode {
	var hitPos: Vec3 = Vec3.ZERO
	var blockHitPos: MutableBlockPos = MutableBlockPos()
	var direction: Direction = Direction.NORTH

	override fun action(level: Level, player: Player, stack: ItemStack) {
		player.rayCast(100.0, blocks())?.let {
			this.hitPos = it.position
			this.blockHitPos.set(it.blockPosition)
			this.direction = it.side
		}
	}

	override fun getUid(): ResourceLocation = this.toolGunLocation("raycast_test")

	override fun getCustomRenderer(): IToolGunModeRenderer = Renderer(this)

	class Renderer(private val mode: RayCastTestMode) : IToolGunModeRenderer {
		override fun getMode(): IToolGunMode = this.mode

		override fun renderOverlayAdditions(
			guiGraphics: GuiGraphics,
			originX: Int,
			originY: Int,
			deltaTracker: DeltaTracker,
			stack: ItemStack,
			data: ToolGunData
		) {
			guiGraphics.drawString(
				this.font,
				"hit pos: ${this.mode.hitPos}",
				originX + 2,
				originY + 80,
				Color.WHITE,
				true
			)
			guiGraphics.drawString(
				this.font,
				"hit blockpos: ${this.mode.blockHitPos}",
				originX + 2,
				originY + 90,
				Color.WHITE,
				true
			)
			guiGraphics.drawString(
				this.font,
				"local blockpos: ${(localClient.player ?: return).blockPosition()}",
				originX + 2,
				originY + 100,
				Color.WHITE,
				true
			)
			guiGraphics.drawString(
				this.font,
				"hit direction: ${this.mode.direction}",
				originX + 2,
				originY + 110,
				Color.WHITE,
				true
			)
		}
	}
}
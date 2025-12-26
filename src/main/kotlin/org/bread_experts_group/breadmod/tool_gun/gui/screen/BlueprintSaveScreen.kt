package org.bread_experts_group.breadmod.tool_gun.gui.screen

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.core.BlockPos
import net.minecraft.data.PackOutput
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtIo
import net.minecraft.nbt.NbtOps
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.structure.BoundingBox
import org.bread_experts_group.breadmod.client.gui.components.GenericEditBox
import org.bread_experts_group.breadmod.client.gui.components.GenericImageButton
import org.bread_experts_group.breadmod.client.gui.screens.PositionedScreen
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.texture.ModGuiElements
import org.bread_experts_group.breadmod.tool_gun.mode.BlueprintMode
import org.bread_experts_group.breadmod.tool_gun.mode.blueprint.Blueprint
import org.bread_experts_group.breadmod.util.Color
import org.bread_experts_group.breadmod.util.component1
import org.bread_experts_group.breadmod.util.component2
import org.bread_experts_group.breadmod.util.component3
import kotlin.io.path.createParentDirectories

class BlueprintSaveScreen(
	val bounding: BoundingBox,
	val blocks: List<Pair<BlockPos, BlockState>>,
	val mode: BlueprintMode
) : PositionedScreen(Component.empty(), 200, 50) {
	val packOutput: PackOutput = PackOutput(BlueprintMode.BLUEPRINT_PATH.createParentDirectories())

	override fun renderBackground(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		this.renderBlurredBackground(partialTick)
		ModGuiElements.BACKGROUND.blitScaled(guiGraphics, this.leftPos, this.topPos, 200, 50)
		guiGraphics.drawString(
			localClient.font,
			"Blocks: ${this.blocks.size}, Width: ${this.bounding.xSpan}, Height: ${this.bounding.ySpan}",
			this.leftPos + 5,
			this.topPos + 30,
			Color.WHITE
		)
		ModGuiElements.FLOPPY_DISK.blit(guiGraphics, this.leftPos, this.topPos)
	}

	override fun isPauseScreen(): Boolean = false

	override fun init() {
		val player = localClient.player ?: return
		val level = player.level()
		super.init()

		this.addRenderableWidget(
			GenericEditBox(
				this.leftPos + 25,
				this.topPos + 5,
				100,
				20,
				Component.empty()
			) { _, _ -> }
		)

		this.addRenderableWidget(
			GenericImageButton(
				this.leftPos + 150,
				this.topPos + 5,
				25,
				25,
				ModGuiElements.FLOPPY_DISK
			) { _, _ ->
				val editBox = this.children().filterIsInstance<GenericEditBox>().first()
				val (x, y, z) = player.position()
				level.playSound(
					player,
					x, y, z,
					SoundEvents.NOTE_BLOCK_BIT.value(),
					SoundSource.AMBIENT,
					1f,
					1.1f
				)
				player.displayClientMessage(Component.literal("Saved blueprint!"), false)
				this.writeNBTtoFile(editBox.value, Blueprint(this.blocks, this.bounding))
				this.mode.pos1.set(0, 0, 0)
				this.mode.pos2.set(0, 0, 0)
				this.onClose()
			}
		)
	}

	fun writeNBTtoFile(fileName: String, blueprint: Blueprint) {
		val encoded = Blueprint.CODEC.encodeStart(NbtOps.INSTANCE, blueprint).orThrow
		val tag = CompoundTag()
		tag.put("blueprint", encoded)
		NbtIo.write(tag, BlueprintMode.BLUEPRINT_PATH.resolve("$fileName.nbt").createParentDirectories())
	}
}
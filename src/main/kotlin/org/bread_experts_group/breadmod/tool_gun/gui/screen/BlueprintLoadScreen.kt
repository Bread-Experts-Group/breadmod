package org.bread_experts_group.breadmod.tool_gun.gui.screen

import com.mojang.math.Axis
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.nbt.NbtIo
import net.minecraft.nbt.NbtOps
import net.minecraft.network.chat.Component
import net.minecraft.world.level.block.Blocks
import org.bread_experts_group.breadmod.client.gui.screens.PositionedScreen
import org.bread_experts_group.breadmod.client.render.getModel
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.redirectFocusFromContainerWidgets
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.client.render.texture.ModGuiElements
import org.bread_experts_group.breadmod.tool_gun.mode.BlueprintMode
import org.bread_experts_group.breadmod.tool_gun.mode.blueprint.Blueprint
import java.nio.file.Path
import kotlin.io.path.listDirectoryEntries

class BlueprintLoadScreen : PositionedScreen(Component.empty()) {
	companion object {
		val blueprints: MutableList<Blueprint> = mutableListOf()
	}

	lateinit var gridList: List<Pair<Int, Int>>

	override fun isPauseScreen(): Boolean = false

	override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean =
		this.redirectFocusFromContainerWidgets(mouseX, mouseY, button)

	override fun renderBackground(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		val poseStack = guiGraphics.pose()
		val bufferSource = guiGraphics.bufferSource()
		this.renderBlurredBackground(partialTick)
		ModGuiElements.BACKGROUND.blitScaled(guiGraphics, this.leftPos, this.topPos, 256, 256)
//		val blueprint = Companion.blueprints[0]

		poseStack.pushPose()
		poseStack.translate(150f, 150f, 200f)
		poseStack.scaleFlat(-20f)
		poseStack.mulPose(Axis.XN.rotationDegrees(10f))
		poseStack.mulPose(Axis.YP.rotationDegrees((localClient.level ?: return).gameTime.toFloat()))
		localClient.blockRenderer.modelRenderer.renderModel(
			poseStack.last(),
			bufferSource.getBuffer(RenderType.solid()),
			Blocks.GRASS_BLOCK.defaultBlockState(),
			localClient.getModel("block/axis"),
			1f,
			1f,
			1f,
			LightTexture.FULL_BRIGHT,
			OverlayTexture.NO_OVERLAY
		)
//		blueprint.blocks.forEach { (pos, state) ->
//			poseStack.pushPose()
//			poseStack.translate(pos)
//			localClient.blockRenderer.renderSingleBlock(
//				state,
//				poseStack,
//				bufferSource,
//				LightTexture.FULL_BRIGHT,
//				OverlayTexture.NO_OVERLAY
//			)
//			poseStack.popPose()
//		}
		poseStack.popPose()

//		repeat(4) { x ->
//			repeat(4) { y ->
//				guiGraphics.borderedFillPositioned(
//					this.leftPos + (x * 52),
//					this.topPos + (y * 52),
//					50,
//					50,
//					Color.BLACK,
//					Color.GRAY
//				)
//			}
//		}
	}

	override fun init() {
		super.init()

		Companion.blueprints.clear()
		Companion.blueprints.addAll(this.getFiles().filter { it.endsWith("2.nbt") }
			.map { this.readNBTFromFile(it) })
//		Companion.blueprints.addAll(this.getFiles().map { this.deserializeBlueprint(it) })
		this.gridList = buildList {
			val left = this@BlueprintLoadScreen.leftPos
			val top = this@BlueprintLoadScreen.topPos
			repeat(4) { y ->
				repeat(4) { x ->
					this.add(x + 2 + left to y + 2 + top)
				}
			}
		}
	}

	fun readNBTFromFile(input: Path): Blueprint {
		val tag = NbtIo.read(input)?.get("blueprint") ?: throw NullPointerException("$input does not exist.")
		return Blueprint.CODEC.decode(NbtOps.INSTANCE, tag).orThrow.first
	}

	fun getFiles(): List<Path> = BlueprintMode.BLUEPRINT_PATH.listDirectoryEntries()
}
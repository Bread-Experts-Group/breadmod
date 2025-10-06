package org.bread_experts_group.breadmod.tool_gun.gui.screen

import com.mojang.blaze3d.platform.InputConstants
import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.AABB
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.client.gui.components.GenericButton
import org.bread_experts_group.breadmod.client.gui.components.GenericEditBox
import org.bread_experts_group.breadmod.client.gui.components.ScrollingContainerWidget
import org.bread_experts_group.breadmod.client.render.arrowTexture
import org.bread_experts_group.breadmod.client.render.drawQuad
import org.bread_experts_group.breadmod.client.render.floorTexture
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.redirectFocusFromContainerWidgets
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.client.render.translate
import org.bread_experts_group.breadmod.network.serverbound.CreateModelBlockItemPacket
import org.bread_experts_group.breadmod.tool_gun.Model
import org.bread_experts_group.breadmod.tool_gun.gui.components.ModelWidget
import org.bread_experts_group.breadmod.util.Color
import kotlin.jvm.optionals.getOrNull

class ModelScreen(val player: Player) : Screen(Component.literal("editor")) {
	companion object {
		fun renderFloor(poseStack: PoseStack, bufferSource: MultiBufferSource, withArrow: Boolean = true) {
			repeat(5) { x ->
				repeat(5) { y ->
					poseStack.pushPose()
					poseStack.translate(x.toDouble(), -y.toDouble(), -0.001)
					drawQuad(poseStack, bufferSource, RenderType.text(floorTexture))
					if (x == 2 && y == 4 && withArrow) {
						poseStack.pushPose()
						poseStack.translate(0.0, -1.1, 0.0)
						drawQuad(poseStack, bufferSource, RenderType.text(arrowTexture))
						poseStack.mulPose(Axis.YN.rotationDegrees(180f))
						poseStack.translate(-1.0, 0.0, 0.0)
						drawQuad(poseStack, bufferSource, RenderType.text(arrowTexture))
						poseStack.popPose()
					}
					poseStack.mulPose(Axis.YN.rotationDegrees(180f))
					poseStack.translate(-1.0, 0.0, 0.0)
					drawQuad(poseStack, bufferSource, RenderType.text(floorTexture))
					poseStack.popPose()
				}
			}
		}

		fun renderBlocks(poseStack: PoseStack, bufferSource: MultiBufferSource, blocks: List<Model>) {
			blocks.forEach { model ->
				poseStack.pushPose()
				poseStack.translate(model.position)
				poseStack.mulPose(model.rotation)
				poseStack.mulPose(Axis.XP.rotationDegrees(90f))
				poseStack.scaleFlat(model.scale)
				localClient.blockRenderer.renderSingleBlock(
					model.state,
					poseStack,
					bufferSource,
					LightTexture.FULL_BRIGHT,
					OverlayTexture.NO_OVERLAY
				)
				if (model.isHighlighted) {
					LevelRenderer.renderLineBox(
						poseStack,
						bufferSource.getBuffer(RenderType.lines()),
						AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0),
						0.5f,
						0.5f,
						1f,
						1f
					)
				}
				poseStack.popPose()
			}
		}

		val blocks: MutableList<Model> = mutableListOf()
	}

	var leftPos: Int = 0
	var topPos: Int = 0
	var floorYaw: Float = 9f
		set(value) {
			field = if (value > 360) -360f else if (value < -360) 360f else value
		}
	var floorPitch: Float = 64f
	var floorRoll: Float = 0f

	override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean =
		this.redirectFocusFromContainerWidgets(mouseX, mouseY, button)

	override fun renderBackground(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		this.renderBlurredBackground(partialTick)
		val poseStack = guiGraphics.pose()
		val bufferSource = guiGraphics.bufferSource()
		Lighting.setupForFlatItems()

		poseStack.pushPose()

		this.translateAndScale(poseStack)
		this.rotateView(poseStack)
		Companion.renderBlocks(poseStack, bufferSource, Companion.blocks)
		Companion.renderFloor(poseStack, bufferSource)

		poseStack.popPose()
	}

	fun translateAndScale(poseStack: PoseStack) {
		poseStack.translate(this.width / 2.0 - 160.0, this.height / 2.0 - 160.0, 0.0)
		poseStack.scaleFlat(-64f)
		poseStack.mulPose(Axis.YN.rotationDegrees(180f))
	}

	fun rotateView(poseStack: PoseStack) {
		poseStack.translate(2.5, -2.5, 0.0)
		poseStack.mulPose(Axis.XN.rotationDegrees(this.floorPitch))
		poseStack.mulPose(Axis.ZN.rotationDegrees(this.floorYaw))
		poseStack.mulPose(Axis.YN.rotationDegrees(this.floorRoll))
		poseStack.translate(-2.5, 2.5, 0.0)
	}

	// cursed
	private fun getYawSlider(): ExtendedSlider =
		this.children().filterIsInstance<ExtendedSlider>().first { it.y == 229 }

	private fun getPitchSlider(): ExtendedSlider =
		this.children().filterIsInstance<ExtendedSlider>().first { it.y == 203 }

	override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, dragX: Double, dragY: Double): Boolean {
		val isHoldingShift = InputConstants.isKeyDown(localClient.window.window, InputConstants.KEY_LSHIFT)
		if (button == 0 && !isHoldingShift) {
			this.floorYaw -= dragX.toFloat()
			this.getYawSlider().value = this.floorYaw.toDouble()
		} else if (button == 0) {
			this.floorPitch -= dragY.toFloat()
			this.getPitchSlider().value = this.floorPitch.toDouble()
		}
		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY)
	}

	override fun isPauseScreen(): Boolean = false

	override fun init() {
		this.leftPos = this.width / 2
		this.topPos = this.height / 2

		this.addRenderableWidget(
			GenericEditBox(
				this.width - 95,
				2,
				93,
				20,
				Component.empty()
			) { editBox, keyCode ->
				if (keyCode == InputConstants.KEY_RETURN) {
					val block =
						BuiltInRegistries.BLOCK.getOptional(ResourceLocation.tryParse(editBox.value))
							.getOrNull()
					if (block != null) {
						Companion.blocks.add(
							Model(
								block.defaultBlockState(),
								block.stateDefinition
							)
						)
						this@ModelScreen.rebuildWidgets()
					}
				}
			}
		)

		this.addRenderableWidget(
			ScrollingContainerWidget<ModelScreen>(
				this.width - 102,
				24,
				100,
				200,
				"models",
				this,
				400,
				Color.BLACK,
				Color.WHITE
			).also { it.init() }
		)

		this.addRenderableWidget(
			object : ExtendedSlider(
				this.width - 62,
				203,
				60,
				25,
				Component.literal("pitch"),
				Component.empty(),
				-360.0,
				360.0,
				this.floorPitch.toDouble(),
				true
			) {
				override fun onDrag(mouseX: Double, mouseY: Double, dragX: Double, dragY: Double) {
					super.onDrag(mouseX, mouseY, dragX, dragY)
					this@ModelScreen.floorPitch = this.valueInt.toFloat()
				}
			}
		)

		this.addRenderableWidget(
			object : ExtendedSlider(
				this.width - 62,
				229,
				60,
				25,
				Component.literal("yaw"),
				Component.empty(),
				-360.0,
				360.0,
				this.floorYaw.toDouble(),
				true
			) {
				override fun onDrag(mouseX: Double, mouseY: Double, dragX: Double, dragY: Double) {
					super.onDrag(mouseX, mouseY, dragX, dragY)
					this@ModelScreen.floorYaw = this.valueInt.toFloat()
				}
			}
		)

		this.addRenderableWidget(
			object : ExtendedSlider(
				this.width - 62,
				255,
				60,
				25,
				Component.literal("roll"),
				Component.empty(),
				-360.0,
				360.0,
				this.floorRoll.toDouble(),
				true
			) {
				override fun onDrag(mouseX: Double, mouseY: Double, dragX: Double, dragY: Double) {
					super.onDrag(mouseX, mouseY, dragX, dragY)
					this@ModelScreen.floorRoll = this.valueInt.toFloat()
				}
			}
		)
		this.addRenderableWidget(GenericButton(this.width - 102, this.height - 42, 100, 40, "EXPORT") { _, _ ->
			PacketDistributor.sendToServer(CreateModelBlockItemPacket(Companion.blocks))
		})

		this.children().filterIsInstance<ScrollingContainerWidget<*>>().find { it.id == "models" }?.let { container ->
			var offset = 0
			Companion.blocks.forEachIndexed { index, model ->
				container.addChild(
					"model_$index",
					ModelWidget(this, model, container.x + 2, container.y + 2 + offset)
				)
				offset += 45
			}
		}
	}
}
package org.bread_experts_group.breadmod.tool_gun.gui.components.creator_widgets

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.core.BlockPos
import net.minecraft.core.component.TypedDataComponent
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RandomSource
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.capabilities.Capabilities
import org.apache.logging.log4j.LogManager
import org.bread_experts_group.breadmod.client.gui.components.ContainerWidget
import org.bread_experts_group.breadmod.client.gui.components.GenericButton
import org.bread_experts_group.breadmod.client.gui.components.GenericEditBox
import org.bread_experts_group.breadmod.client.render.LerpTicker
import org.bread_experts_group.breadmod.client.render.LerpTicker.LerpParams
import org.bread_experts_group.breadmod.client.render.borderedFillPositioned
import org.bread_experts_group.breadmod.tool_gun.gui.components.creator_widgets.data.BlockEntityDataWidget
import org.bread_experts_group.breadmod.tool_gun.gui.components.creator_widgets.data.CapabilityDataWidget
import org.bread_experts_group.breadmod.tool_gun.gui.components.creator_widgets.data.StatePropertyWidget
import org.bread_experts_group.breadmod.tool_gun.gui.screen.CreatorScreen
import org.bread_experts_group.breadmod.util.Color
import org.bread_experts_group.breadmod.util.getCapability
import org.bread_experts_group.breadmod.util.logDebugInfo
import org.bread_experts_group.breadmod.util.putBlockState
import kotlin.jvm.optionals.getOrNull

// todo three buttons to select between blockstate, block entity, and capability editing (also have icons for them).
class BlockTab(screen: CreatorScreen, val level: Level) : ContainerWidget<CreatorScreen>(
	screen.leftPos,
	screen.topPos + 15,
	256,
	225,
	"block_tab",
	screen
) {
	val lerpTicker: LerpTicker<Int> = LerpTicker(0 to LerpParams())
	var blockEntity: BlockEntity? = null
	private val random = RandomSource.create()

	init {
		this.init()
	}

	override fun renderContainer(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		guiGraphics.borderedFillPositioned(this.x, this.y, this.width, this.height, Color.WHITE, Color.BLACK)
		CreatorScreen.renderBlockPreview(
			this.screen.currentBlock,
			this.blockEntity,
			guiGraphics,
			this.random,
			this.x + 155,
			this.y + 1,
			this.lerpTicker.getLerpedValue(0, partialTick),
			partialTick
		)
		this.setBlockEntityFromState()
	}

	override fun tickAdditional() {
		this.lerpTicker.tickAllPositions()
	}

	fun setBlockEntityFromState() {
		val state = this.screen.currentBlock
		if (this.blockEntity != null) return
		val entityBlock = state.block as? EntityBlock ?: return
		val blockEntity = entityBlock.newBlockEntity(BlockPos.ZERO, state) ?: return
		blockEntity.level = this.level
		this.blockEntity = blockEntity
	}

	fun killDataWidgets() {
		this.screen.currentBlock.properties.forEach {
			this.removeChild("property_container_${it.name}")
		}
		this.blockEntity?.saveCustomOnly(this.level.registryAccess())?.tags?.forEach { (key, _) ->
			this.removeChild("be_data_container_$key")
		}
		this.removeChild("cap_widget_item_handler")
		this.removeChild("cap_widget_energy_handler")
		this.removeChild("cap_widget_fluid_handler")
	}

	fun populateStatePropertyWidgets() {
		var offsetY = 0
		this.screen.currentBlock.properties.forEach { property ->
			this.addChild(
				"property_container_${property.name}",
				StatePropertyWidget(this.screen, this.x + 5, this.y + 25 + offsetY, property)
			)
			offsetY += 42
		}
	}

	fun populateBlockEntityDataWidgets() {
		var offsetY = 0
		this.blockEntity?.let { entity ->
			val data = entity.saveCustomOnly(this.level.registryAccess())
			if (data.isEmpty) return@let
			data.tags.forEach { (key, tag) ->
				this.addChild(
					"be_data_container_$key",
					BlockEntityDataWidget(this.screen, this.x + 5, this.y + 25 + offsetY, key, tag),
					isActive = false,
					shouldRender = false
				)
				offsetY += 42
			}
		}
	}

	fun <T> getCapability(capability: BlockCapability<T, *>): T? =
		this.blockEntity?.let { this.level.getCapability(capability, BlockPos.ZERO, this.screen.currentBlock, it) }

	fun populateCapabilityWidgets() {
		var offsetY = 0
		this.getCapability(Capabilities.ItemHandler.BLOCK)?.let {
			this.addChild(
				"cap_widget_item_handler",
				CapabilityDataWidget(this.screen, this, this.x + 5, this.y + 25, it),
				isActive = false,
				shouldRender = false
			)
			offsetY += 42
		}
		this.getCapability(Capabilities.EnergyStorage.BLOCK)?.let {
			this.addChild(
				"cap_widget_energy_handler",
				CapabilityDataWidget(this.screen, this, this.x + 5, this.y + 25 + offsetY, it),
				isActive = false,
				shouldRender = false
			)
			offsetY += 42
		}
		this.getCapability(Capabilities.FluidHandler.BLOCK)?.let {
			this.addChild(
				"cap_widget_fluid_handler",
				CapabilityDataWidget(this.screen, this, this.x + 5, this.y + 25 + offsetY, it),
				isActive = false,
				shouldRender = false
			)
			offsetY += 42
		}
	}

	private fun statePropertyWidgetVisibility(visible: Boolean) {
		this.screen.currentBlock.properties.forEach {
			(this.getChild("property_container_${it.name}") as? StatePropertyWidget<*>)?.setState(visible)
		}
	}

	private fun beDataWidgetVisibility(visible: Boolean) {
		this.blockEntity?.let {
			val data = it.saveCustomOnly(this.level.registryAccess())
			data.tags.forEach { (key, _) ->
				(this.getChild("be_data_container_$key") as? BlockEntityDataWidget<*>)?.setState(visible)
			}
		}
	}

	private fun capabilityWidgetVisibility(visible: Boolean) {
		(this.getNullableChild("cap_widget_item_handler") as? CapabilityDataWidget<*>)?.setState(visible)
		(this.getNullableChild("cap_widget_energy_handler") as? CapabilityDataWidget<*>)?.setState(visible)
		(this.getNullableChild("cap_widget_fluid_handler") as? CapabilityDataWidget<*>)?.setState(visible)
	}

	private fun stateFromString(value: String): BlockState =
		BuiltInRegistries.BLOCK.getOptional(ResourceLocation.tryParse(value)).getOrNull()
			?.defaultBlockState() ?: Blocks.AIR.defaultBlockState()

	override fun initContainer() {
		this.setBlockEntityFromState()
		this.populateStatePropertyWidgets()
		this.populateBlockEntityDataWidgets()
		this.populateCapabilityWidgets()
		this.addChild(
			"send_button",
			GenericButton(this.x + 195, this.y + 192, 60, 12, "Send to server") { _, _ ->
				this.screen.data.setValueDirect { it.putBlockState("block", this.screen.currentBlock) }
			}
		)
		this.addChild(
			"edit_box", GenericEditBox(
				this.x + 155,
				this.y + 204,
				100,
				20,
				Component.literal("test"), { it.setMaxLength(100) }, { editBox, keyCode ->
					if (keyCode == InputConstants.KEY_RETURN) {
						this.killDataWidgets()
						this.blockEntity = null
						this.screen.currentBlock = this.stateFromString(editBox.value)
						this.setBlockEntityFromState()
						this.populateStatePropertyWidgets()
						this.populateBlockEntityDataWidgets()
						this.populateCapabilityWidgets()
						this.blockEntity?.let { entity ->
							val data = entity.saveCustomOnly(this.level.registryAccess())
							data.tags.forEach { (key, tag) ->
								if (tag is CompoundTag) {
									tag.tags.forEach { (cKey, cTag) ->
										logDebugInfo("parent: $key, $cKey, ${cTag.type}")
									}
								}
								logDebugInfo("$key, ${tag.type}")
							}
							logDebugInfo(
								this.level.getCapability(
									Capabilities.EnergyStorage.BLOCK,
									BlockPos.ZERO,
									this.screen.currentBlock,
									entity
								)?.maxEnergyStored
							)
							entity.components().forEach<TypedDataComponent<*>>(LogManager.getLogger("BlockTab")::info)
						}
					}
				})
		)

		this.addChild(
			"blockstate_button",
			GenericButton(this.x + 1, this.y + 1, 20, 20, "B", Component.literal("Blockstate")) { _, _ ->
				this.statePropertyWidgetVisibility(true)
				this.beDataWidgetVisibility(false)
				this.capabilityWidgetVisibility(false)
			}
		)
		this.addChild(
			"block_entity_button",
			GenericButton(this.x + 21, this.y + 1, 20, 20, "BE", Component.literal("BlockEntity")) { _, _ ->
				this.statePropertyWidgetVisibility(false)
				this.beDataWidgetVisibility(true)
				this.capabilityWidgetVisibility(false)
			}
		)
		this.addChild(
			"capability_button",
			GenericButton(this.x + 41, this.y + 1, 20, 20, "C", Component.literal("Capabilities")) { _, _ ->
				this.statePropertyWidgetVisibility(false)
				this.beDataWidgetVisibility(false)
				this.capabilityWidgetVisibility(true)
			}
		)
		this.addChild(
			"nbt_button",
			GenericButton(this.x + 61, this.y + 1, 25, 20, "NBT", Component.literal("(read-only)")) { _, _ ->
				this.statePropertyWidgetVisibility(false)
				this.beDataWidgetVisibility(false)
				this.capabilityWidgetVisibility(false)
			}
		)
	}
}
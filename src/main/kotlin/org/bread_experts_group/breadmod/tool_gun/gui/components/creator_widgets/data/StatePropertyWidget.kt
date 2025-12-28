package org.bread_experts_group.breadmod.tool_gun.gui.components.creator_widgets.data

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.level.block.state.properties.Property
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.client.gui.components.ContainerWidget
import org.bread_experts_group.breadmod.client.gui.components.GenericButton
import org.bread_experts_group.breadmod.client.render.borderedFillPositioned
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.tool_gun.gui.screen.CreatorScreen
import org.bread_experts_group.breadmod.util.Color
import java.lang.reflect.Method

class StatePropertyWidget<T : Property<*>>(
	screen: CreatorScreen,
	x: Int,
	y: Int,
	private val property: T
) : ContainerWidget<CreatorScreen>(
	x,
	y,
	140,
	40,
	"property_widget",
	screen
) {
	private val logger: Logger = LogManager.getLogger("Property Widget Logger")
	private val setValueMethod: Method =
		BlockState::class.java.getMethod("setValue", Property::class.java, Comparable::class.java)

	override fun renderContainer(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		guiGraphics.borderedFillPositioned(this.x, this.y, this.width, this.height, Color.LIGHT_GRAY, Color.GRAY)
		guiGraphics.drawString(
			localClient.font,
			"${this.property.name}: ${this.screen.currentBlock.getValue(this.property)}",
			this.x + 2,
			this.y + 2,
			Color.WHITE
		)
	}

	// todo internal compiler error is being caused here for some reason, might be a kotlin bug
	override fun initContainer() {
//		when (this.property) {
//			is IntegerProperty -> {
//				var xOffset = 0
//				var yOffset = 0
//				this.property.allValues.toList().forEach { intValue ->
//					this.addChild(
//						"${this.property.name}_${intValue.value}",
//						this.makeButton(xOffset, yOffset, intValue.value.toString()) { _, _ ->
//							this.setInt(this.property, intValue.value)
//						}
//					)
//					if (intValue.value == 2) {
//						yOffset = 14
//						xOffset -= 132
//					}
//					xOffset += 44
//				}
//			}
//			is BooleanProperty -> {
//				var xOffset = 0
//				this.property.allValues.toList().forEach { boolValue ->
//					this.addChild(
//						"${this.property.name}_${boolValue.value}",
//						this.makeButton(xOffset, 0, boolValue.value.toString()) { _, _ ->
//							this.setBool(this.property, boolValue.value)
//						}
//					)
//					xOffset += 44
//				}
//			}
//			is DirectionProperty -> {
//				var xOffset = 0
//				var yOffset = 0
//				Direction.entries.forEach { direction ->
//					this.addChild(
//						"${this.property.name}_${direction.name}",
//						this.makeButton(xOffset, yOffset, direction.name) { _, _ ->
//							this.setDirection(this.property, direction)
//						},
//						isActive = direction in this.property.possibleValues
//					)
//					if (Direction.entries.indexOf(direction) == 2) {
//						yOffset = 14
//						xOffset -= 132
//					}
//					xOffset += 44
//				}
//			}
//			is EnumProperty<*> -> {
//				var xOffset = 0
//				var yOffset = 0
//				this.ordinalsFromEnumProperty(this.property).forEach { ordinal ->
//					val enum = this.property.possibleValues.toList()[ordinal]
//					this.addChild(
//						"${this.property.name}_$ordinal",
//						this.makeButton(xOffset, yOffset, enum.name) { _, _ -> this.setEnum(this.property, ordinal) }
//					)
//					if (ordinal == 2) {
//						yOffset = 14
//						xOffset -= 132
//					}
//					xOffset += 44
//				}
//			}
//			else -> this.logger.warn("unhandled property type: ${this.property}, report to mod dev.")
//		}
	}

	private fun makeButton(xOffset: Int, yOffset: Int, text: String, onClick: (Button, Int) -> Unit): GenericButton =
		GenericButton(
			this.x + 6 + xOffset,
			this.y + 12 + yOffset,
			40,
			12,
			text,
			Component.empty(),
			onClick
		)

	private fun setEnum(property: EnumProperty<*>, ordinal: Int) {
		val constants = this.screen.currentBlock.getValue(property)::class.java.enumConstants
		if (ordinal in constants.indices)
			this.screen.currentBlock = this.setValueMethod.invoke(
				this.screen.currentBlock,
				property,
				constants[ordinal]
			) as BlockState
	}

	private fun ordinalsFromEnumProperty(property: EnumProperty<*>): IntRange =
		this.screen.currentBlock.getValue(property)::class.java.enumConstants.indices

	private fun setBool(property: BooleanProperty, value: Boolean) {
		this.screen.currentBlock = this.screen.currentBlock.setValue(property, value)
	}

	private fun setDirection(property: DirectionProperty, direction: Direction) {
		if (direction in property.possibleValues)
			this.screen.currentBlock = this.screen.currentBlock.setValue(property, direction)
	}

	private fun setInt(property: IntegerProperty, value: Int) {
		if (value in property.possibleValues)
			this.screen.currentBlock = this.screen.currentBlock.setValue(property, value)
	}
}
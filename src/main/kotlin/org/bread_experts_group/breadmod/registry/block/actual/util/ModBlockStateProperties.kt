package org.bread_experts_group.breadmod.registry.block.actual.util

import net.minecraft.core.Direction
import net.minecraft.util.StringRepresentable
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.IntegerProperty

object ModBlockStateProperties {
	val STORAGE_LEVEL: IntegerProperty = IntegerProperty.create("storage_level", 0, 13)
	val TRIPLE_BLOCK: EnumProperty<TripleBlockHalf> =
		EnumProperty.create("triple_half", TripleBlockHalf::class.java)
	val UPGRADE_ONE: BooleanProperty = BooleanProperty.create("upgrade_one")
	val UPGRADE_TWO: BooleanProperty = BooleanProperty.create("upgrade_two")
	val UPGRADE_THREE: BooleanProperty = BooleanProperty.create("upgrade_three")

	enum class TripleBlockHalf(
		private val directionToOther: Direction,
		private val flippedMiddleDirection: Direction?
	) : StringRepresentable {
		LOWER(Direction.UP, null),
		MIDDLE(Direction.UP, Direction.DOWN),
		UPPER(Direction.DOWN, null);

		fun getDirectionToOther(flipMiddle: Boolean = false): Direction =
			if (flipMiddle && this == MIDDLE) this.flippedMiddleDirection ?: Direction.DOWN else this.directionToOther

		fun getOtherHalf(flipMiddle: Boolean = false): TripleBlockHalf =
			when (this) {
				LOWER -> MIDDLE
				MIDDLE -> if (flipMiddle) LOWER else UPPER
				UPPER -> MIDDLE
			}

		override fun toString(): String = this.serializedName

		override fun getSerializedName(): String = when (this) {
			LOWER -> "lower"
			UPPER -> "upper"
			MIDDLE -> "middle"
		}
	}
}
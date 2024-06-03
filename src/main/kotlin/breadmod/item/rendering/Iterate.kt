package breadmod.item.rendering

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Direction.Axis
import java.util.*

/*
* Code credit goes to the Create team
* https://github.com/Creators-of-Create/Create
*/

class Iterate {
    val trueAndFalse: BooleanArray = booleanArrayOf(true, false)
    val falseAndTrue: BooleanArray = booleanArrayOf(false, true)
    val zeroAndOne: IntArray = intArrayOf(0, 1)
    val positiveAndNegative: IntArray = intArrayOf(1, -1)
    val directions: Array<Direction> = Direction.entries.toTypedArray()
    val horizontalDirections: Array<Direction?> = getHorizontals()
    val axes = Direction.Axis.entries.toTypedArray()
    val axisSet: EnumSet<Direction.Axis> = EnumSet.allOf(Direction.Axis::class.java)

    private fun getHorizontals(): Array<Direction?> {
        val directions = arrayOfNulls<Direction>(4)
        for (i in 0..3) directions[i] = Direction.from2DDataValue(i)
        return directions
    }

    fun directionsInAxis(pAxis: Axis) = when(pAxis) {
        Axis.X -> arrayOf(Direction.EAST, Direction.WEST)
        Axis.Y -> arrayOf(Direction.UP, Direction.DOWN)
        else -> arrayOf(Direction.SOUTH, Direction.NORTH)
    }

    fun hereAndBelow(pPos: BlockPos) = listOf(pPos, pPos.below())
    fun hereBelowAndAbove(pPos: BlockPos) = listOf(pPos, pPos.below(), pPos.above())

    fun <T> cycleValue(list: List<T>, current: T): T {
        val currentIndex = list.indexOf(current)
        require(currentIndex != -1) { "Current value not found in list" }
        val nextIndex = (currentIndex + 1) % list.size
        return list[nextIndex]
    }
}
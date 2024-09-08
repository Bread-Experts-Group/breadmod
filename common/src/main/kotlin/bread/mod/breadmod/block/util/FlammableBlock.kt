package bread.mod.breadmod.block.util

import net.minecraft.world.level.block.Block

/**
 * Marker annotation for blocks that can catch fire and burn.
 * @param encouragement The chance that a neighboring block will catch fire.
 * @param flammability The chance that this block will catch fire.
 * @throws IllegalArgumentException If the [encouragement] or [flammability] is less than zero, or if the annotated
 * element is not a [Block].
 * @author Miko Elbrecht
 * @since 1.0.0
 */
@Target(AnnotationTarget.FIELD)
annotation class FlammableBlock(val encouragement: Int, val flammability: Int)

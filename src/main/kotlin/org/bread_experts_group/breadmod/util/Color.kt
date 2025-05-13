package org.bread_experts_group.breadmod.util

object Color {
	val WHITE: Int = this.color(255, 255, 255)
	val BLACK: Int = this.color()
	val GRAY: Int = this.color(128, 128, 128)
	val LIGHT_GRAY: Int = this.color(192, 192, 192)
	val DARK_GRAY: Int = this.color(64, 64, 64)
	val RED: Int = this.color(255)
	val PINK: Int = this.color(255, 175, 175)
	val ORANGE: Int = this.color(255, 200)
	val YELLOW: Int = this.color(255, 255)
	val GREEN: Int = this.color(g = 255)
	val MAGENTA: Int = this.color(r = 255, b = 255)
	val CYAN: Int = this.color(g = 255, b = 255)
	val BLUE: Int = this.color(b = 255)

	fun color(r: Int = 0, g: Int = 0, b: Int = 0, a: Int = 255): Int =
		(((a and 0xFF) shl 24) or ((r and 0xFF) shl 16) or ((g and 0xFF) shl 8) or ((b and 0xFF) shl 0))
	//	private val testWhite: Int = 0x00FFFFFF or (0xFF shl 24)
}
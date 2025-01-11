@file:Suppress("unused")

package org.bread_experts_group.breadmod.logging

enum class GraphicsModes(val value: Int) {
	// Graphics
	BOLD(1),
	DIM(2),
	ITALIC(3),
	UNDERLINE(4),
	BLINKING(5),
	REVERSE(7),
	HIDDEN(8),
	STRIKETHROUGH(9),
	BACKGROUND(10),
	BRIGHT(60),

	// Colors
	BLACK(30),
	RED(31),
	GREEN(32),
	YELLOW(33),
	BLUE(34),
	MAGENTA(35),
	CYAN(36),
	WHITE(37)
}

enum class GraphicsModesResets(val value: Int) {
	// Graphics Resets
	BOLD_DIM_RESET(22),
	ITALIC_RESET(23),
	UNDERLINE_RESET(24),
	BLINKING_RESET(25),
	REVERSE_RESET(27),
	HIDDEN_RESET(28),
	STRIKETHROUGH_RESET(29),

	// Colors Resets
	FG_RESET(39),
	BG_RESET(49),

	// Master Reset
	RESET(0)
}

const val ANSI_CONTROL_SEQUENCE_ESCAPE: String = "\u001B["
const val ANSI_GRAPHICS_END: String = "m"
infix fun GraphicsModes.join(text: String): String =
	"$ANSI_CONTROL_SEQUENCE_ESCAPE${this.value}${ANSI_GRAPHICS_END}$text"

infix fun GraphicsModes.join(char: Char): String = this.join(char.toString())
typealias ChainedMode = MutableList<GraphicsModes>

infix fun GraphicsModes.set(mode: GraphicsModes): ChainedMode = mutableListOf(this, mode)
infix fun ChainedMode.set(mode: GraphicsModes): ChainedMode = this.also { it.add(mode) }
infix fun ChainedMode.join(text: String): String =
	"$ANSI_CONTROL_SEQUENCE_ESCAPE${this.joinToString(";") { it.value.toString() }}${ANSI_GRAPHICS_END}$text"

infix fun ChainedMode.join(char: Char): String = this.join(char.toString())
infix fun String.reset(reset: GraphicsModesResets): String =
	"$this$ANSI_CONTROL_SEQUENCE_ESCAPE${reset.value}${ANSI_GRAPHICS_END}"
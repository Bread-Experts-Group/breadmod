package breadmod.commands

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType

object CommandArguments {
    class IntArgument : ArgumentType<Int> {
        override fun parse(reader: StringReader): Int = reader.readInt()
    }
    class BooleanArgument : ArgumentType<Boolean> {
        override fun parse(reader: StringReader): Boolean = reader.readBoolean()
    }
}
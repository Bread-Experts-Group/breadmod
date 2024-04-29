package breadmod.rnd

import breadmod.rnd.util.ExtendedArray

class Memory(val size: Long) {
    val data = ExtendedArray<Byte>(size, 0)

    override fun toString(): String = "[size=$size, data=$data]"
}
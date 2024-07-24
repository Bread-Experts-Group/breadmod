package breadmod.rnd.riscv32

/**
 * A register to store a value (or locking to a certain value, for example, a zero register.)
 *
 * @author Miko Elbrecht
 * @see 1.0.0
 */
data class Register(private val lock: Int? = null, val saver: RegisterSaver) {
    var storedValue: Int = 0
        get() = lock ?: field
        internal set

    override fun toString(): String = "Register(${if(lock != null) "lock=$lock, " else ""}saver=$saver, storedValue=$storedValue)"
}
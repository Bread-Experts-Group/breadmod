package breadmod.util.riscv32

/**
 * A register to store a value (or locking to a certain value, e.g. a zero register.)
 *
 * @author Miko Elbrecht
 * @see 1.0.0
 */
class Register(private val lock: Int? = null) {
    var storedValue: Int = 0
        get() = lock ?: field
        internal set
}
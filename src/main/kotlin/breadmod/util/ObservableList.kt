package breadmod.util

/**
 * A list that can be observed for changes.
 *
 * @param E The type of elements in the list.
 * @param size The size of the list.
 * @param listChanged The callback to be called when the list is changed.
 *
 * @author Miko Elbrecht
 * @since 1.0.0
 */
open class ObservableList<E>(
    override val size: Int,
    var listChanged: ((index: Int, element: E) -> Unit)? = null
) : AbstractMutableList<E>() {
    constructor(list: List<E>, changed: ((index: Int, element: E) -> Unit)? = null) : this(list.size, changed) {
        this.list.addAll(list)
    }

    private val list: MutableList<E> = mutableListOf()

    override fun add(index: Int, element: E) {
        list.add(index, element)
        listChanged?.invoke(index, element)
    }

    override fun get(index: Int): E = list[index]

    override fun removeAt(index: Int): E {
        val removed = list.removeAt(index)
        listChanged?.invoke(index, removed)
        return removed
    }

    override fun set(index: Int, element: E): E {
        val previous = list.set(index, element)
        listChanged?.invoke(index, element)
        return previous
    }
}
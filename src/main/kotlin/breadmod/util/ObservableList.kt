package breadmod.util

/**
 * A list that can be observed for changes.
 *
 * @param E The type of elements in the list.
 * @param listSize The size of the list.
 * @param listChanged The callback to be called when the list is changed.
 *
 * @author Miko Elbrecht
 * @since 1.0.0
 */
open class ObservableList<E>(
    listSize: Int,
    initialization: (index: Int) -> E,
    var listChanged: ((index: Int, element: E) -> Unit)? = null
) : AbstractMutableList<E>() {
    constructor(list: List<E>, changed: ((index: Int, element: E) -> Unit)? = null) : this(
        list.size,
        { list[it] },
        changed
    )

    private val list: MutableList<E> = MutableList(listSize, initialization)
    override val size: Int = list.size

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
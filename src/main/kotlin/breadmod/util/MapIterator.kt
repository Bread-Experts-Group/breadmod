package breadmod.util

class MapIterator<K, V>(map: Map<K, V>) : Iterator<Map.Entry<K, V>> {
    private val entryList: List<Map.Entry<K, V>> = map.entries.toList()
    private var currentIndex: Int = -1

    fun current(): Map.Entry<K, V> = entryList[currentIndex]
    override fun hasNext(): Boolean = currentIndex + 1 < entryList.size

    override fun next(): Map.Entry<K, V> {
        if (!hasNext()) throw NoSuchElementException()
        if (currentIndex == -1) currentIndex = 0
        return entryList[currentIndex++]
    }

    fun saveState(): Int = currentIndex
    fun restoreState(index: Int) {
        currentIndex = index
    }
}
package fel.cvut.cz.omo.sem.reports

// for every cycle that is not in the map returns list from previous cycle in the map (map stores only changes)
class CycleIterator<T>(map: Map<Int,List<T>>, private val lastCycle: Int) {

    private val mapToIterate = map.filter{it.key<lastCycle}.toSortedMap()
    private var index = 0
    private var mapIndex = 0
    private val mapIterator = mapToIterate.iterator()
    private var first = mapToIterate.firstKey()
    private var current = mapNextCycle()
    private var next = mapNextCycle()

    fun hasNext(): Boolean {
        return index <= lastCycle-mapToIterate.firstKey()
    }

    private fun mapHasNext(): Boolean {
        return mapIndex < mapToIterate.size
    }

    private fun mapNextCycle(): Int? {
        val mapNext = if (mapHasNext()) mapIterator.next().key else null
        mapIndex++
        return mapNext
    }

    fun next(): List<T> {
        if (next != null && index == (next!!-first)) {
            current = next
            next = mapNextCycle()
        }
        index++
        return mapToIterate[current]!!
    }
}
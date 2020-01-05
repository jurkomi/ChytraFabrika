package fel.cvut.cz.omo.sem.events

class EventContainer {
    val events = mutableListOf<Event>()

    fun addEvent(event: Event) {
        events.add(event)
    }

    fun removeEvent(event: Event) {
        events.remove(event)
    }

    fun solveEvents() {
        events.sortedBy { it.priority }.forEach { if (it.solve()) events.remove(it) }
    }
}
package fel.cvut.cz.omo.sem.events

import fel.cvut.cz.omo.sem.Factory

class EventContainer(private val factory: Factory) {
    private val events = mutableListOf<Event>()
    private val eventHistory = mutableMapOf<Int, MutableList<Event>>()

    fun addEvent(event: Event) {
        events.add(event)
        val cycle = factory.cycleCounter
        if (eventHistory.containsKey(cycle)) eventHistory[cycle]!!.add(event)
        else eventHistory[cycle] = mutableListOf(event)
    }

    fun removeEvent(event: Event) {
        events.remove(event)
    }

    fun solveEvents() {
        events.sortedBy { it.priority }.forEach { if (it.solve()) removeEvent(it)}
    }

    fun getEventsForPeriod(startCycle: Int, endCycle: Int): Map<Int, List<Event>> {
        return eventHistory.filter { it.key in startCycle..endCycle }
    }
}
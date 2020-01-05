package fel.cvut.cz.omo.sem.events

import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.Observer
import fel.cvut.cz.omo.sem.production.LinePriority
import fel.cvut.cz.omo.sem.production.SequenceItem
import fel.cvut.cz.omo.sem.resources.Unit
import javax.naming.Context

class SequenceNotAvailableEvent(private val factory: Factory, private val context: Observer, private val linePriority: LinePriority?, registeredUnits: List<Observer>? = null) : Event(factory, context, linePriority, registeredUnits) {

    init {
        println("Sequence not available.")
        factory.eventContainer.addEvent(this)
    }

    override fun solve(): Boolean {
        val b = (context as SequenceItem).getAssignedUnits().isEmpty()
        if (b) UnitAvailable(factory, context, linePriority, registeredUnits.toList())
        return b
    }
}
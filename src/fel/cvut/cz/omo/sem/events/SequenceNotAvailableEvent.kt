package fel.cvut.cz.omo.sem.events

import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.Observer
import fel.cvut.cz.omo.sem.production.LinePriority
import fel.cvut.cz.omo.sem.production.SequenceItem

class SequenceNotAvailableEvent(private val factory: Factory,
                                private val context: Observer,
                                private val linePriority: LinePriority?,
                                registeredUnits: List<Observer>? = null
) : Event(factory, context, linePriority, registeredUnits) {

    override val origin = (context as SequenceItem).itemLabel

    init {
        println("Sequence not available.")
        factory.eventContainer.addEvent(this)
    }

    override fun solve(): Boolean {
        val b = (context as SequenceItem).assignUnitsAndGet().isNotEmpty()
        if (b) UnitAvailableEvent(factory, context, linePriority, registeredUnits.toList())
        return b
    }

    override fun getContext(): Observer {
        return context
    }
}
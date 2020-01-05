package fel.cvut.cz.omo.sem.events

import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.Observer
import fel.cvut.cz.omo.sem.production.LinePriority
import javax.naming.Context

class BatteryChangedEvent(private val factory: Factory, val context: Observer, linePriority: LinePriority? = null, registeredUnits: List<Observer>? = null) : Event(factory, context, linePriority, registeredUnits) {

    init {
        factory.eventContainer.addEvent(this)
    }

    override fun solve(): Boolean {
        return true
    }
}
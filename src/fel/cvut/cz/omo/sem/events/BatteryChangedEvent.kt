package fel.cvut.cz.omo.sem.events

import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.Observer
import fel.cvut.cz.omo.sem.production.LinePriority
import fel.cvut.cz.omo.sem.resources.equipment.Equipment

class BatteryChangedEvent(private val factory: Factory,
                          private val context: Observer,
                          linePriority: LinePriority? = null,
                          registeredUnits: List<Observer>? = null
) : Event(factory, context, linePriority, registeredUnits) {

    override val origin = (context as Equipment).name

    init {
        factory.eventContainer.addEvent(this)
    }

    override fun solve(): Boolean {
        return true
    }

    override fun getContext(): Observer {
        return context
    }
}
package fel.cvut.cz.omo.sem.events

import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.Observer
import fel.cvut.cz.omo.sem.production.LinePriority
import fel.cvut.cz.omo.sem.resources.Unit
import javax.naming.Context

class ChangeBatteryEvent(private val factory: Factory, val context: Observer, private val linePriority: LinePriority? = null, registeredUnits: List<Observer>? = null) : Event(factory, context, linePriority, registeredUnits) {

    init {
        println("UBattery needs to be changed.")
        factory.eventContainer.addEvent(this)
    }

    override fun solve(): Boolean {
        BatteryChangedEvent(factory, context, linePriority, registeredUnits)
        return true
    }
}
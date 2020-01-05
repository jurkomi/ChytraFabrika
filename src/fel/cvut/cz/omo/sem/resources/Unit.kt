package fel.cvut.cz.omo.sem.resources

import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.FactoryItem
import fel.cvut.cz.omo.sem.Observer
import fel.cvut.cz.omo.sem.Visitable
import fel.cvut.cz.omo.sem.events.Event
import fel.cvut.cz.omo.sem.production.ProductionLine

abstract class Unit(val factory: Factory, val name: String, val purchasePrice: Double) : Resource(factory, name, purchasePrice), Execution, FactoryItem, Observer,
    Visitable {

    abstract val hourlyCost: Double
    val eventsRegistered: MutableList<Event> = mutableListOf()
    protected var pline: ProductionLine? = null

    fun registerToEvent(event: Event) {
        eventsRegistered.add(event)
    }

    fun unregisterFromEvent(event: Event) {
        eventsRegistered.remove(event)
    }

    abstract fun isAvailable(): Boolean

    abstract fun addToProductionLineSequence(productionLine: ProductionLine): Boolean

    abstract fun removeFromProductionLineSequence(productionLine: ProductionLine)

}
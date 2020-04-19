package fel.cvut.cz.omo.sem.resources

import com.google.gson.annotations.Expose
import fel.cvut.cz.omo.sem.*
import fel.cvut.cz.omo.sem.events.Event
import fel.cvut.cz.omo.sem.production.ProductionLine

abstract class Unit(
    factory: Factory,
    @Expose val name: String,
    @Expose val purchasePrice: Double,
    private val id: Int? = null
) : Resource(factory, name, purchasePrice), Execution, FactoryItem, Observer, Visitable {

    companion object {
        var i: Int = 0
    }

    @Expose val unitId: Int = id ?: ++i

    init {
        if (id != null && id > i) i = id
    }

    abstract val hourlyCost: Double
    private val eventsRegistered: MutableList<Event> = mutableListOf()
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
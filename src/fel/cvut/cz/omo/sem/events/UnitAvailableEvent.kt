package fel.cvut.cz.omo.sem.events

import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.Observer
import fel.cvut.cz.omo.sem.production.LinePriority
import fel.cvut.cz.omo.sem.production.SequenceItem
import fel.cvut.cz.omo.sem.resources.Unit

class UnitAvailableEvent(private val factory: Factory,
                         private val context: Observer,
                         linePriority: LinePriority? = null,
                         registeredUnits: List<Observer>? = null
) : Event(factory, context, linePriority, registeredUnits) {

    override val origin: String
        get() {
            return when (context) {
                is Unit -> context.name
                is SequenceItem -> context.itemLabel
                else -> "unknown"
            }
        }

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
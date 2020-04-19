package fel.cvut.cz.omo.sem.events

import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.Observer
import fel.cvut.cz.omo.sem.production.LinePriority
import fel.cvut.cz.omo.sem.resources.equipment.Equipment
import fel.cvut.cz.omo.sem.resources.people.Repairman

class RepairedEvent(private val factory: Factory,
                    private val context: Observer,
                    linePriority: LinePriority?,
                    registeredUnits: List<Observer>? = null
) : Event(factory, context, linePriority, registeredUnits) {

    override val origin: String
        get() {
            registeredUnits.forEach { if (it is Equipment) return it.name }
            return "unknown"
        }

    override fun getRepairmanName(): String? {
        return (context as Repairman).name
    }

    init {
        println("Cycle ${factory.cycleCounter}: Equipment was succesfully repaired by ${(context as Repairman).name}.")
        factory.eventContainer.addEvent(this)
    }

    override fun solve(): Boolean {
        return true
    }

    override fun getContext(): Observer {
        return context
    }
}
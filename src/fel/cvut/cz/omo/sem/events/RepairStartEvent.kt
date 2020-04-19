package fel.cvut.cz.omo.sem.events

import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.Observer
import fel.cvut.cz.omo.sem.resources.people.Repairman
import fel.cvut.cz.omo.sem.production.LinePriority
import fel.cvut.cz.omo.sem.resources.equipment.Equipment

class RepairStartEvent(private val factory: Factory,
                       private val context: Observer,
                       private val linePriority: LinePriority?,
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
        factory.eventContainer.addEvent(this)
    }

    private var progress = 0

    override fun solve():Boolean {
        progress += (context as Repairman).getRepairProgress()
        println("Repair progress is $progress")
        return if (progress >=  100) {
            RepairedEvent(factory, context, linePriority, registeredUnits)
            true
        } else false
    }

    override fun getContext(): Observer {
        return context
    }
}
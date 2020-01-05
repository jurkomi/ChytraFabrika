package fel.cvut.cz.omo.sem.events

import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.Observer
import fel.cvut.cz.omo.sem.resources.persons.Repairman
import fel.cvut.cz.omo.sem.production.LinePriority

class RepairStartEvent(private val factory: Factory, private val context: Observer, private val linePriority: LinePriority?, registeredUnits: List<Observer>? = null) : Event(factory, context, linePriority, registeredUnits
) {

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
}
package fel.cvut.cz.omo.sem.events

import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.Observer
import fel.cvut.cz.omo.sem.resources.people.Repairman
import fel.cvut.cz.omo.sem.production.LinePriority
import fel.cvut.cz.omo.sem.resources.equipment.Equipment

class BrokenEvent(private val factory: Factory,
                  private val context: Observer,
                  private val linePriority: LinePriority?,
                  registeredUnits: List<Observer>? = null
) : Event(factory, context, linePriority, registeredUnits) {

    override val origin = (context as Equipment).name

    override fun getRepairmanName(): String? {
        return if (repairman == null) null else repairman!!.name
    }

    init {
        println("Cycle ${factory.cycleCounter}: Equipment ${(context as Equipment).name} is broken.")
        factory.eventContainer.addEvent(this)
    }
    private var repairman: Repairman? = null

    override fun solve(): Boolean {
        if (repairman == null) {
            repairman = factory.employeeContainer.getRepairman()
        }
        return if (repairman == null) false
            else {
                println("Repairman ${repairman!!.name} registered to event.")
                register(repairman!!)
                RepairStartEvent(factory, repairman!!, linePriority, registeredUnits).register(context as Equipment)
                true
            }
    }

    override fun getContext(): Observer {
        return context
    }

}
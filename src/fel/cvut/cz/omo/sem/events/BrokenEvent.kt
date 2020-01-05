package fel.cvut.cz.omo.sem.events

import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.Observer
import fel.cvut.cz.omo.sem.resources.persons.Repairman
import fel.cvut.cz.omo.sem.production.LinePriority
import fel.cvut.cz.omo.sem.resources.equipment.Equipment

class BrokenEvent(private val factory: Factory, private val context: Observer, private val linePriority: LinePriority?, registeredUnits: List<Observer>? = null) : Event(factory, context, linePriority, registeredUnits
) {

    init {
        println("Tact ${factory.tactCounter}: Equipment ${(context as Equipment).name} is broken.")
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

}
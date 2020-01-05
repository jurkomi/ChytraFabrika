package fel.cvut.cz.omo.sem.events

import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.Observer
import fel.cvut.cz.omo.sem.production.LinePriority
import fel.cvut.cz.omo.sem.resources.equipment.Equipment
import fel.cvut.cz.omo.sem.resources.persons.Repairman

class RepairedEvent(private val factory: Factory, private val context: Observer, linePriority: LinePriority?, registeredUnits: List<Observer>? = null) : Event(factory, context, linePriority, registeredUnits) {

    init {
        println("Tact ${factory.tactCounter}: Equipment was succesfully repaired by ${(context as Repairman).name}.")
        factory.eventContainer.addEvent(this)
    }

    override fun solve(): Boolean {
        return true
    }
}
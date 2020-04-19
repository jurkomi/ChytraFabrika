package fel.cvut.cz.omo.sem.production

import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.Observer
import fel.cvut.cz.omo.sem.events.Event
import fel.cvut.cz.omo.sem.resources.Unit
import fel.cvut.cz.omo.sem.resources.Material
import fel.cvut.cz.omo.sem.resources.equipment.Equipment
import fel.cvut.cz.omo.sem.resources.equipment.EquipmentState
import fel.cvut.cz.omo.sem.resources.people.PersonState
import fel.cvut.cz.omo.sem.resources.people.Workman

abstract class SequenceItem(private val factory: Factory,
                            val itemType: SequenceItemType,
                            val itemLabel: String,
                            val itemQuantity: Int,
                            val material: Material,
                            val materialQuantity: Double) : Observer {

    protected val assignedUnit: MutableList<Unit> = mutableListOf()

    fun assignUnitsAndGet():List<Unit> {
        if(assignedUnit.size != itemQuantity) {
            val b = assignAutomatically()
            return if (b) assignedUnit else mutableListOf()
        }
        return assignedUnit
    }

    fun assignUnit(unit: Unit) {
        if (unit.isAvailable()) assignedUnit.add(unit)
        else throw IllegalAccessException("Unit is not available.")
        if (assignedUnit.size > itemQuantity) assignedUnit.removeAt(0)
    }

    abstract fun assignAutomatically(): Boolean

    fun clearAssignedUnits() {
        assignedUnit.clear()
    }

    override fun updateState(event: Event) {
        assignedUnit.forEach { unit -> unit.updateState(event) }
    }

    fun getAssignedUnits(): List<Unit> {
        return assignedUnit.toList()
    }
}
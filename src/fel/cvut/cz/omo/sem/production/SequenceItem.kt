package fel.cvut.cz.omo.sem.production

import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.Observer
import fel.cvut.cz.omo.sem.events.Event
import fel.cvut.cz.omo.sem.resources.persons.PersonState
import fel.cvut.cz.omo.sem.resources.persons.Workman
import fel.cvut.cz.omo.sem.resources.Unit
import fel.cvut.cz.omo.sem.resources.Material
import fel.cvut.cz.omo.sem.resources.equipment.Equipment
import fel.cvut.cz.omo.sem.resources.equipment.EquipmentState

class SequenceItem(private val factory: Factory, private val itemType: SequenceItemType, private val itemLabel: String, private val itemQuantity: Int, val material: Material, val materialQuantity: Double) : Observer {

    private val assignedUnit: MutableList<Unit> = mutableListOf()

    fun getAssignedUnits():List<Unit> {
        if(assignedUnit.size != itemQuantity) {
            val b = assignAutomatically()
            return if (b) assignedUnit else mutableListOf()
        }
        return assignedUnit
    }

    fun assignUnit(unit: Unit) {
        when(unit) {
            is Workman -> if(unit.state == PersonState.AVAILABLE) {
                assignedUnit.add(unit)
            }
                        else throw Error("Person is not available.")
            is Equipment -> if (unit.state == EquipmentState.STANDBY || unit.state == EquipmentState.OFF || unit.state == PersonState.AVAILABLE) {
                assignedUnit.add(unit)
            }
                        else throw Error("Equipment is not available.")
        }
        if (assignedUnit.size > itemQuantity) assignedUnit.removeAt(0)
    }

    fun assignAutomatically(): Boolean {
        when(itemType) {
            SequenceItemType.PERSON -> {
                val workmen = factory.employeeContainer.getWorkmen(itemQuantity-assignedUnit.size)
                return if (workmen != null) {
                    assignedUnit.addAll(workmen)
                    true
                } else false
            }
            SequenceItemType.EQUIPMENT -> {
                val equipment = factory.equipmentContainer.getEquipmentByName(itemLabel,itemQuantity-assignedUnit.size)
                return if (equipment != null) {
                    assignedUnit.addAll(equipment)
                    true
                } else false
            }
        }
    }

    fun clearAssignedUnits() {
        assignedUnit.clear()
    }

    override fun updateState(event: Event) {
    }
}
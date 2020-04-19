package fel.cvut.cz.omo.sem.production

import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.resources.Material

class SequenceItemPerson(private val factory: Factory,
                         itemQuantity: Int,
                         material: Material,
                         materialQuantity: Double) : SequenceItem(factory, SequenceItemType.PERSON, "Workman", itemQuantity, material, materialQuantity) {

    override fun assignAutomatically(): Boolean {
        val workmen = factory.employeeContainer.getWorkmen(itemQuantity-assignedUnit.size)
        return if (workmen != null) {
            assignedUnit.addAll(workmen)
            true
        } else false
    }

}
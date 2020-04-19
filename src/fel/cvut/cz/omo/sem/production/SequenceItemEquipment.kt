package fel.cvut.cz.omo.sem.production

import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.resources.Material

class SequenceItemEquipment(private val factory: Factory,
                            itemLabel: String,
                            itemQuantity: Int,
                            material: Material,
                            materialQuantity: Double) : SequenceItem(factory, SequenceItemType.EQUIPMENT, itemLabel, itemQuantity, material, materialQuantity) {

    override fun assignAutomatically(): Boolean {
        val equipment =
            factory.equipmentContainer.getEquipmentByName(itemLabel,itemQuantity-assignedUnit.size)
        return if (equipment != null) {
            assignedUnit.addAll(equipment)
            true
        } else false
    }

}
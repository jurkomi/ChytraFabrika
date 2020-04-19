package fel.cvut.cz.omo.sem.production

import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.resources.Material

class SequenceItemFactory {

    fun getSequenceItem(factory: Factory, itemType: SequenceItemType, itemLabel: String, itemQuantity: Int,
                        material: Material, materialQuantity: Double): SequenceItem {
        return when(itemType) {
            SequenceItemType.PERSON -> SequenceItemPerson(factory, itemQuantity, material, materialQuantity)
            SequenceItemType.EQUIPMENT ->
                SequenceItemEquipment(factory, itemLabel, itemQuantity, material, materialQuantity)
        }
    }

}
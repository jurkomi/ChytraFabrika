package fel.cvut.cz.omo.sem.resources.equipment

import fel.cvut.cz.omo.sem.Factory

class PluggedEquipment(factory: Factory,
                       name: String,
                       purchasePrice: Double,
                       numberOfOperators: Int,
                       energyConsumptionPerHour: Double,
                       oilConsumptionPerHour: Double
) : Equipment(factory, name, purchasePrice, numberOfOperators, energyConsumptionPerHour, oilConsumptionPerHour) {

    init {
        factory.addEquipment(this)
        factory.equipmentContainer.addEquipment(this)
    }

}
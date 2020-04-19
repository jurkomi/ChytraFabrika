package fel.cvut.cz.omo.sem.resources.equipment

import fel.cvut.cz.omo.sem.resources.Material

class EquipmentApi {

    fun isFunctional(equipment: Equipment): Boolean {
        return equipment.isFunctional()
    }

    fun getEnergyConsumption(equipment: Equipment, cycleNumber: Int): Double {
        return equipment.getEnergyConsumptionForCycle(cycleNumber)
    }

    fun getOilConsumption(equipment: Equipment, cycleNumber: Int): Double {
        return equipment.getOilConsumptionForCycle(cycleNumber)
    }

    fun getMaterialConsumption(equipment: Equipment, cycleNumber: Int): Map<Material, Double> {
        return equipment.getMaterialConsumptionForCycle(cycleNumber)
    }

    fun getEnergyConsumptionForPeriod(equipment: Equipment, startCycleNumber: Int, endCycleNumber: Int): Double {
        return equipment.getEnergyConsumptionForPeriod(startCycleNumber, endCycleNumber)
    }

    fun getOilConsumptionForPeriod(equipment: Equipment, startCycleNumber: Int, endCycleNumber: Int): Double {
        return equipment.getOilConsumptionForPeriod(startCycleNumber, endCycleNumber)
    }

    fun getMaterialConsumptionForPeriod(equipment: Equipment, startCycleNumber: Int, endCycleNumber: Int): Map<Material, Double> {
        return equipment.getMaterialConsumptionForPeriod(startCycleNumber, endCycleNumber)
    }

    fun returnEquipmentStateToCycle(equipment: Equipment, cycleNumber: Int) {
        return equipment.returnEquipmentStateToCycle(cycleNumber)
    }
}
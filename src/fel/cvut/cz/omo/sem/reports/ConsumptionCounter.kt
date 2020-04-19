package fel.cvut.cz.omo.sem.reports

import fel.cvut.cz.omo.sem.resources.Material

interface ConsumptionCounter {

    val energyConsumptionHistory: MutableMap<Int, Double>
    val oilConsumptionHistory: MutableMap<Int, Double>
    val materialConsumptionHistory: MutableMap<Int, Map<Material,Double>>

    fun getEnergyConsumptionForCycle(cycleNumber: Int): Double {
        return if (energyConsumptionHistory.containsKey(cycleNumber)) energyConsumptionHistory[cycleNumber]!! else 0.0
    }
    fun getOilConsumptionForCycle(cycleNumber: Int): Double {
        return if (oilConsumptionHistory.containsKey(cycleNumber)) oilConsumptionHistory[cycleNumber]!! else 0.0
    }
    fun getMaterialConsumptionForCycle(cycleNumber: Int): Map<Material,Double> {
        return if (materialConsumptionHistory.containsKey(cycleNumber)) materialConsumptionHistory[cycleNumber]!! else mapOf()
    }
    fun getEnergyConsumptionForPeriod(startCycleNumber: Int, endCycleNumber:Int): Double {
        var energy = 0.0
        energyConsumptionHistory.filter{it.key in startCycleNumber..endCycleNumber}.forEach{energy += it.value}
        return energy
    }
    fun getOilConsumptionForPeriod(startCycleNumber: Int, endCycleNumber:Int): Double {
        var oil = 0.0
        oilConsumptionHistory.filter{it.key in startCycleNumber..endCycleNumber}.forEach{oil += it.value}
        return oil
    }
    fun getMaterialConsumptionForPeriod(startCycleNumber: Int, endCycleNumber:Int): Map<Material,Double> {
        val material = mutableMapOf<Material,Double>()
        materialConsumptionHistory.filter{it.key in startCycleNumber..endCycleNumber}.forEach{
            it.value.forEach{ materialMap ->
                material[materialMap.key] = material.getOrDefault(materialMap.key, 0.0) + materialMap.value
            }
        }
        return material
    }
}
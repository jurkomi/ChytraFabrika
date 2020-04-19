package fel.cvut.cz.omo.sem.reports

import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.resources.Material
import fel.cvut.cz.omo.sem.resources.equipment.EquipmentApi

class ConsumptionReport(val factory: Factory) : Report(factory) {

    private val equipmentApi = EquipmentApi()

    override val defaultFileName: String = "ConsumptionReport"

    override fun generateReport(startCycleNumber: Int, endCycleNumber: Int, fileName: String) {
        addToReport("Consumption report for cycles $startCycleNumber-$endCycleNumber:\n")
        var energyConsumption = 0.0
        var oilConsumption = 0.0
        val materialConsumption = mutableMapOf<Material,Double>()
        addToReport("Equipment consumption:")
        factory.getEquipment().forEach {equipment ->
            val oil = equipmentApi.getOilConsumptionForPeriod(equipment, startCycleNumber, endCycleNumber)
            val energy = equipmentApi.getEnergyConsumptionForPeriod(equipment, startCycleNumber, endCycleNumber)
            val material = equipmentApi.getMaterialConsumptionForPeriod(equipment, startCycleNumber, endCycleNumber)
            generateReportEntry(equipment.name, energy, oil, material)
        }
        addToReport("\nProduction line consumption:")
        factory.getProductionLines().forEach { line ->
            val energy = line.getEnergyConsumptionForPeriod(startCycleNumber,endCycleNumber)
            val oil = line.getOilConsumptionForPeriod(startCycleNumber,endCycleNumber)
            val material = line.getMaterialConsumptionForPeriod(startCycleNumber,endCycleNumber)
            energyConsumption += energy
            oilConsumption += oil
            material.forEach { materialMap ->
                materialConsumption[materialMap.key] =
                    materialConsumption.getOrDefault(materialMap.key, 0.0) + materialMap.value
            }
            generateReportEntry(line.label, energy, oil, material)
        }
        addToReport("\nFactory consumption:")
        addToReport("\tEnergy consumption: $energyConsumption (${factory.energyCost*energyConsumption} ${factory.currency})")
        addToReport("\tOil consumption: $oilConsumption (${factory.oilCost*oilConsumption} ${factory.currency})")
        var materialConsumptionString = ""
        materialConsumption.forEach{
            materialConsumptionString += "${it.key.name} ${it.value} " +
                    "(${it.value*it.key.purchasePrice} ${factory.currency}), "
        }
        addToReport("\tMaterial Consumption: ${materialConsumptionString.substring(0,materialConsumptionString.length-2)}")

        super.generateReport(startCycleNumber, endCycleNumber, fileName)
    }

    private fun generateReportEntry(name: String, energy: Double, oil: Double, material: Map<Material,Double>) {
        var materialString = ""
        if (material.isEmpty()) materialString = "no consumption. "
        material.forEach{
            materialString += "${it.key.name} ${it.value} " +
                    "(${it.value*it.key.purchasePrice} ${factory.currency}), "
        }
        addToReport("\t$name:\n" +
                "\t\tEnergyConsumption: $energy (${factory.energyCost*energy} ${factory.currency})\n" +
                "\t\tOil Consumption: $oil (${factory.oilCost * oil} ${factory.currency})\n" +
                "\t\tMaterial Consumption: ${materialString.substring(0,materialString.length-2)}")
    }

}
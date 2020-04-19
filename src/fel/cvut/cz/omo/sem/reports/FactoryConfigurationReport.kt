package fel.cvut.cz.omo.sem.reports

import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.resources.equipment.Equipment
import fel.cvut.cz.omo.sem.resources.equipment.EquipmentType
import fel.cvut.cz.omo.sem.resources.people.Person

class FactoryConfigurationReport(val factory: Factory) : Report(factory) {

    override val defaultFileName: String = "FactoryConfigurationReport"

    override fun generateReport(startCycleNumber: Int, endCycleNumber: Int, fileName: String) {
        addToReport("Factory configuration report for cycles $startCycleNumber-$endCycleNumber:\n")
        addToReport("Factory ${factory.name} configuration:")
        val cycleIterator = CycleIterator(
            factory.getProductionLinesForPeriod(
                startCycleNumber,
                endCycleNumber
            ), endCycleNumber
        )
        var cycle = startCycleNumber
        while(cycleIterator.hasNext()) {
            addToReport("Cycle $cycle:")
            cycleIterator.next().forEach { line ->
                val product = line.getProductForCycle(cycle)
                val productName = product?.name ?: "no product"
                addToReport("\tProduction line ${line.label}: product $productName")
                var sequenceString = "Sequence: "
                val sequence = line.getProductionSequenceForCycle(cycle)
                if (sequence.isEmpty()) sequenceString += "no sequence, "
                else {
                    sequence.forEach { unit ->
                        val type = when (unit) {
                            is Person -> "Person"
                            is Equipment -> {
                                if (unit.type == EquipmentType.ROBOT) "Robot" else "Machine"
                            }
                            else -> Unit
                        }
                        sequenceString += "$type ${unit.name}, "
                    }
                }
                addToReport("\t\t${sequenceString.substring(0,sequenceString.length-2)}")
            }
            cycle++
        }

        super.generateReport(startCycleNumber, endCycleNumber, fileName)
    }

}
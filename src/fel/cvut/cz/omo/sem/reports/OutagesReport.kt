package fel.cvut.cz.omo.sem.reports

import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.Observer
import fel.cvut.cz.omo.sem.events.*
import fel.cvut.cz.omo.sem.production.ProductionLine

class OutagesReport(factory: Factory) : Report(factory) {

    override val defaultFileName: String = "OutagesReport"

    private val allOutages = mutableListOf<Pair<Int,Int>>()
    private var averageWaitingTime = 0
    private var waitingCounter = 0
    private val brokenOutage = Outage<ProductionLine>("broken equipment")
    private val batteryOutage = Outage<ProductionLine>("drained battery")
    private val unavailableOutage = Outage<Pair<ProductionLine,Observer>>("unavailable units")

    private inner class Outage<T>(val reason: String) {
        val outages = mutableListOf<Pair<Int,Int>>()
        val newOutages = mutableMapOf<T, Int>()
        var duration = 0

        // used by generateReport fun to process finished and unfinished outages to compute its duration
        fun getOutagesDurationForPeriod(startCycleNumber: Int, endCycleNumber: Int): Int {
            var length = 0
            outages.forEach {
                if (it.first in startCycleNumber until endCycleNumber || it.second in (startCycleNumber + 1)..endCycleNumber) {
                    val outageStart = if (it.first < startCycleNumber) startCycleNumber else it.first
                    val outageEnd = if (it.second < endCycleNumber) it.second else endCycleNumber
                    allOutages.add(Pair(outageStart,outageEnd))
                    length += outageEnd - outageStart
                }
            }
            newOutages.values.toList().forEach{
                if (it < endCycleNumber) {
                    val outageStart = if (it < startCycleNumber) startCycleNumber else it
                    allOutages.add(Pair(outageStart, endCycleNumber))
                    length += endCycleNumber - outageStart
                }
            }
            return length
        }

        fun finishOutage(cycleNumber: Int, key: T) {
            val startCycle = newOutages[key]
            newOutages.remove(key)
            if (startCycle !=null) {
                duration += startCycle
                outages.add(Pair(startCycle,cycleNumber))
            }
        }
    }

    // adding start of new outage
    fun addNewOutage(cycleNumber: Int, pline: ProductionLine, event: Event) {
        when(event) {
            is BrokenEvent -> brokenOutage.newOutages[pline] = cycleNumber
            is ChangeBatteryEvent -> batteryOutage.newOutages[pline] = cycleNumber
            is SequenceNotAvailableEvent, is UnitNotAvailableEvent -> unavailableOutage.newOutages[Pair(pline, event.getContext())] = cycleNumber
        }

    }

    // add data to report a save it to a file
    override fun generateReport(startCycleNumber: Int, endCycleNumber: Int, fileName: String) {
        addToReport("Outages report for cycles $startCycleNumber-$endCycleNumber:\n")
        allOutages.clear()
        val brokenOutagesLength = brokenOutage.getOutagesDurationForPeriod(startCycleNumber, endCycleNumber)
        val batteryOutagesLength = batteryOutage.getOutagesDurationForPeriod(startCycleNumber, endCycleNumber)
        val unavailabilityOutagesLength = unavailableOutage.getOutagesDurationForPeriod(startCycleNumber, endCycleNumber)
        if (allOutages.isEmpty()) addToReport("no outage data")
        else {
            allOutages.sortByDescending { it.second - it.first }
            val longest = allOutages.first().second - allOutages.first().first
            val shortest = allOutages.last().second - allOutages.last().first
            val average = allOutages.sumBy { it.second - it.first }.toDouble() / allOutages.size
            addToReport("Longest outage duration: $longest")
            addToReport("Shortest outage duration: $shortest")
            addToReport("Average outage duration: $average")
            addToReport("Average waiting time for a repairman: $averageWaitingTime")
            val outagesLengths = mapOf(brokenOutage.reason to brokenOutagesLength,
                batteryOutage.reason to batteryOutagesLength, unavailableOutage.reason to unavailabilityOutagesLength)
            outagesLengths.entries.sortedByDescending { it.value }.forEach { (reason, length) ->
                addToReport("Outages due to $reason duration: $length")
            }
        }

        super.generateReport(startCycleNumber, endCycleNumber, fileName)
    }

    // saves start and end cycle of outage if outage has finished,
    // computes average waiting time for repairman for ReapirStartEvent
    fun addOutageUpdate(cycleNumber: Int, pline: ProductionLine, event: Event) {
        when(event){
            is RepairedEvent -> brokenOutage.finishOutage(cycleNumber, pline)
            is RepairStartEvent ->  {
                val startCycle = brokenOutage.newOutages[pline]
                if (startCycle !=null) {
                    waitingCounter++
                    averageWaitingTime =
                        (averageWaitingTime * (waitingCounter-1) + (cycleNumber - startCycle))/waitingCounter
                }
            }
            is BatteryChangedEvent -> batteryOutage.finishOutage(cycleNumber, pline)
            is UnitAvailableEvent -> unavailableOutage.finishOutage(cycleNumber, Pair(pline, event.getContext()))
        }
    }

}
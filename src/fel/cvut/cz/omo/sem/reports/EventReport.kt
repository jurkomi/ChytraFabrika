package fel.cvut.cz.omo.sem.reports

import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.events.Event

class EventReport(private val factory: Factory) : Report(factory) {

    override val defaultFileName: String = "EventReport"

    override fun generateReport(startCycleNumber: Int, endCycleNumber: Int, fileName: String) {
        addToReport("Event report for cycles $startCycleNumber-$endCycleNumber:\n")
        val events = factory.eventContainer.getEventsForPeriod(startCycleNumber,endCycleNumber)
        if (events.isEmpty()) addToReport("No events arose during given period.")
        addToReport("Events grouped by type:")
        val typeGroup = mutableMapOf<String, String>()
        val originGroup = mutableMapOf<String,MutableMap<Int,MutableList<Event>>>()
        val repairmanGroup = mutableMapOf<String,MutableMap<Int,MutableList<Event>>>()
        events.forEach { (cycle, list) ->
            list.forEach { event ->
                // type grouping
                typeGroup[event.javaClass.simpleName] =
                    typeGroup.getOrDefault(event.javaClass.simpleName, "") + "$cycle, "
                // origin grouping
                val originMap = originGroup.getOrDefault(event.origin, mutableMapOf())
                if (originMap.containsKey(cycle)) originMap[cycle]!!.add(event)
                else originMap[cycle] = mutableListOf(event)
                originGroup[event.origin] = originMap
                // repairman grouping
                val name = event.getRepairmanName()
                if (name != null) {
                    val repairmanMap = repairmanGroup.getOrDefault(name, mutableMapOf())
                    if (repairmanMap.containsKey(cycle)) repairmanMap[cycle]!!.add(event)
                    else repairmanMap[cycle] = mutableListOf(event)
                    repairmanGroup[name] = repairmanMap
                }
            }
        }
        // type grouping
        typeGroup.toSortedMap().forEach { (eventType, cycles) ->
            addToReport("$eventType: ${cycles.substring(0,cycles.length-2)}")
        }
        addToReport("")
        // origin grouping
        addToReport("Events grouped by origin:")
        originGroup.toSortedMap().forEach { (origin, eventMap) ->
            var events = ""
            eventMap.forEach { (cycle, eventList) ->
                events += "\n\tCycle $cycle: "
                eventList.forEach { events += "\n\t\t${it.javaClass.simpleName}: priority ${it.priority.name.toLowerCase()}" }
                events = events.substring(0,events.length)
            }
            addToReport("$origin:$events")
        }
        addToReport("")
        // repairman grouping
        addToReport("Events grouped by repairman:")
        repairmanGroup.toSortedMap().forEach { (repairman, eventMap) ->
            var events = ""
            eventMap.forEach { (cycle, eventList) ->
                events += "\n\tCycle $cycle: "
                eventList.forEach { events += "\n\t\t${it.javaClass.simpleName}: origin ${it.origin}, priority ${it.priority.name.toLowerCase()}" }
                events = events.substring(0,events.length)
            }
            addToReport("$repairman:$events")
        }

        super.generateReport(startCycleNumber, endCycleNumber, fileName)
    }
}
package fel.cvut.cz.omo.sem.resources.equipment

import com.google.gson.annotations.Expose
import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.Visitable
import fel.cvut.cz.omo.sem.Visitor
import fel.cvut.cz.omo.sem.events.*
import fel.cvut.cz.omo.sem.production.ProductionLine
import fel.cvut.cz.omo.sem.reports.ConsumptionCounter
import fel.cvut.cz.omo.sem.resources.Material
import fel.cvut.cz.omo.sem.resources.Unit
import kotlin.random.Random

abstract class Equipment(factory: Factory,
                         name: String,
                         purchasePrice: Double,
                         @Expose private val numberOfOperators: Int,
                         @Expose protected val energyConsumptionPerHour: Double,
                         @Expose private val oilConsumptionPerHour: Double,
                         id: Int?
) : Unit(factory, name, purchasePrice, id), Visitable, ConsumptionCounter {

    var state = EquipmentState.OFF
        protected set
    val type = if (numberOfOperators > 0) EquipmentType.MACHINE else EquipmentType.ROBOT
    var functionalRate = 100.0
        protected set
    private var energyConsumption = 0.0
    private var oilConsumption = 0.0
    override val hourlyCost: Double = energyConsumptionPerHour*0.004+oilConsumptionPerHour*50
    private val materialsUsed: MutableMap<Material, Double> = mutableMapOf()
    override val energyConsumptionHistory: MutableMap<Int, Double> = mutableMapOf()
    override val oilConsumptionHistory: MutableMap<Int, Double> = mutableMapOf()
    override val materialConsumptionHistory: MutableMap<Int, Map<Material, Double>> = mutableMapOf()
    protected val eventHistory: MutableMap<Int, MutableList<Event>> = mutableMapOf()

    fun isFunctional(): Boolean {
        return (state == EquipmentState.OFF || state == EquipmentState.STANDBY || state == EquipmentState.RUNNING)
    }

    override fun isAvailable(): Boolean {
        return (state == EquipmentState.OFF || state == EquipmentState.STANDBY)
    }

    override fun doWork(material: Material, quantity: Double) {
        if (state == EquipmentState.RUNNING) {
                material.use(quantity, this)
                materialConsumptionHistory[factory.cycleCounter] = mapOf(Pair(material,quantity))
                materialsUsed[material] = materialsUsed.getOrDefault(material, 0.0)+quantity
        }
    }

    protected open fun consumeEnergy(energy: Double) {
        energyConsumption += energy
        energyConsumptionHistory[factory.cycleCounter] = energy // save consumption data for cycle
    }

    /*
    fun use(prodline: ProductionLine, vararg operators: List<Workman>) {
        if (operators.size != numberOfOperators)
            throw Error("Number of operators for this equipment is $numberOfOperators")
        if (pline != null) throw Error("Equipment is already in use")
        this.pline = prodline
        state = EquipmentState.STANDBY
    }

    fun turnOff(pline: ProductionLine) {
        if (pline == this.pline) {
            this.pline = null
            state = EquipmentState.OFF
        }
    }
*/
    override fun updateState(event: Event) {
        when(event) {
            is BrokenEvent -> state = EquipmentState.BROKEN
            is RepairStartEvent -> state = EquipmentState.IN_REPAIR
            is RepairedEvent -> {
                functionalRate = 100.0
                state = if (pline == null) EquipmentState.STANDBY else EquipmentState.RUNNING
            }
            is ChangeBatteryEvent -> state = EquipmentState.OFF
            is BatteryChangedEvent -> state = if (pline == null) EquipmentState.STANDBY else EquipmentState.RUNNING
        }
        val events = eventHistory.getOrDefault(factory.cycleCounter, mutableListOf())
        events.add(event)
        eventHistory[factory.cycleCounter] = events
    }

    override fun addToProductionLineSequence(productionLine: ProductionLine): Boolean {
        return if (state == EquipmentState.STANDBY || state == EquipmentState.OFF) {
            pline = productionLine
            AddEquipmentToProductionLineEvent(factory, pline!!, pline!!.priority, listOf(this))
            state = EquipmentState.RUNNING
            true
        } else false
    }

    override fun removeFromProductionLineSequence(productionLine: ProductionLine) {
        if (pline == productionLine && state == EquipmentState.RUNNING) {
            RemoveEquipmentFromProductionLineEvent(factory, pline!!, pline!!.priority, listOf(this))
            state = EquipmentState.OFF
            pline = null
        }
        else throw IllegalAccessException("Equipment is not part of the given production line.")
    }

    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    override fun nextCycle() {
        when (state) {
            EquipmentState.STANDBY -> consumeEnergy(2.0)
            EquipmentState.OFF -> if (this is BatteryEquipment) consumeEnergy(0.1)
            EquipmentState.RUNNING -> {
                consumeEnergy(energyConsumptionPerHour)
                oilConsumption += oilConsumptionPerHour
                oilConsumptionHistory[factory.cycleCounter] = oilConsumptionPerHour // save consumption data for cycle
                functionalRate -= functionalRate*Random.nextDouble(0.0005, 0.05)
                if (functionalRate < 10.0) {
                    BrokenEvent(factory, this, pline!!.priority, listOf(pline!!))
                }
            }
            else -> {}
        }
    }

    open fun returnEquipmentStateToCycle(cycleNumber: Int) {
        val currentPline = pline
        val currentState = state
        pline = null
        state = EquipmentState.OFF
        functionalRate = 100.0
        energyConsumption = 0.0
        oilConsumption = 0.0
        materialsUsed.clear()
        materialConsumptionHistory.filter { it.key <= cycleNumber }.values.forEach{ materialMap ->
            materialMap.forEach { (material, quantity) ->
                materialsUsed[material] = materialsUsed.getOrDefault(material, 0.0) + quantity
            }
        }
        // iterate through eve  nts to get equipment state
        val eventsInPeriod = eventHistory.filter{it.key <= cycleNumber}
        eventsInPeriod.values.forEach { list ->
            list.forEach { event ->
                when (event) {
                    is AddEquipmentToProductionLineEvent -> {
                        pline = event.getContext() as ProductionLine
                        state = EquipmentState.RUNNING
                    }
                    is BrokenEvent -> state = EquipmentState.BROKEN
                    is RemoveEquipmentFromProductionLineEvent -> {
                        pline = null
                        state = EquipmentState.OFF
                    }
                    is RepairedEvent -> {
                        functionalRate = 100.0
                        state = if (pline == null) EquipmentState.STANDBY else EquipmentState.RUNNING
                    }
                    is RepairStartEvent -> state = EquipmentState.IN_REPAIR
                    is ChangeBatteryEvent -> state = EquipmentState.OFF
                    is BatteryChangedEvent ->
                        state = if (pline == null) EquipmentState.STANDBY else EquipmentState.RUNNING
                }
                when (state) {
                    EquipmentState.STANDBY -> energyConsumption += 2.0
                    EquipmentState.RUNNING -> {
                        functionalRate -= functionalRate*Random.nextDouble(0.0005, 0.05)
                        energyConsumption += energyConsumptionPerHour
                        oilConsumption += oilConsumptionPerHour
                    }
                    else -> {}
                }
            }
        }
        // if equipment is part of some production line, put it where it belongs
        pline = currentPline
        state = currentState

    }

}
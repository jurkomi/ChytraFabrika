package fel.cvut.cz.omo.sem.resources.equipment

import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.Visitable
import fel.cvut.cz.omo.sem.Visitor
import fel.cvut.cz.omo.sem.events.*
import fel.cvut.cz.omo.sem.resources.persons.Workman
import fel.cvut.cz.omo.sem.production.ProductionLine
import fel.cvut.cz.omo.sem.resources.Material
import fel.cvut.cz.omo.sem.resources.Unit
import kotlin.random.Random

abstract class Equipment(factory: Factory,
                         name: String,
                         purchasePrice: Double,
                         private val numberOfOperators: Int,
                         private val energyConsumptionPerHour: Double,
                         private val oilConsumptionPerHour: Double) : Unit(factory, name, purchasePrice), Visitable {

    var state = EquipmentState.OFF
        protected set
    val type = if (numberOfOperators > 0) EquipmentType.MACHINE else EquipmentType.ROBOT
    var functionalRate = 100.0
        protected set
    private var energyConsumption = 0.0
    private var oilConsumption = 0.0
    override val hourlyCost: Double = energyConsumptionPerHour*0.004+oilConsumptionPerHour*50
    private val materialsUsed: MutableMap<Material, Double> = mutableMapOf()

    fun isFunctional(): Boolean {
        return (state == EquipmentState.OFF || state == EquipmentState.STANDBY || state == EquipmentState.RUNNING)
    }

    override fun isAvailable(): Boolean {
        return (state == EquipmentState.OFF || state == EquipmentState.STANDBY)
    }

    fun getEnergyConsumption(): Double {
        return energyConsumption
    }

    fun getOilConsumption(): Double {
        return oilConsumption
    }

    fun getMaterialConsumption(): Map<Material, Double> {
        return materialsUsed
    }

    override fun doWork(material: Material, quantity: Double) {
        if (state == EquipmentState.RUNNING) {
                material.use(quantity, this)
                materialsUsed[material] = materialsUsed.getOrDefault(material, 0.0)+quantity
        }
    }

    protected open fun consumeEnergy(energy: Double) {
        energyConsumption += energy
    }

    fun use(pline: ProductionLine, vararg operators: List<Workman>) {
        if (operators.size != numberOfOperators) throw Error("Number of operators for this equipment is $numberOfOperators")
        if (pline != null) throw Error("Equipment is already in use")
        this.pline = pline
        state = EquipmentState.STANDBY
    }

    fun turnOff(pline: ProductionLine) {
        if (pline == this.pline) {
            this.pline = null
            state = EquipmentState.OFF
        }
    }

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
    }

    override fun addToProductionLineSequence(productionLine: ProductionLine): Boolean {
        return if (state == EquipmentState.STANDBY || state == EquipmentState.OFF) {
            pline = productionLine
            state = EquipmentState.RUNNING
            true
        } else false
    }

    override fun removeFromProductionLineSequence(productionLine: ProductionLine) {
        if (pline == productionLine && state == EquipmentState.RUNNING) {
            state = EquipmentState.OFF
            pline = null
        }
        else throw IllegalAccessException("Equipment is not part of the given production line.")
    }

    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    override fun nextTact() {
        when (state) {
            EquipmentState.STANDBY -> consumeEnergy(2.0)
            EquipmentState.OFF -> if (this is BatteryEquipment) consumeEnergy(0.1)
            EquipmentState.RUNNING -> {
                consumeEnergy(energyConsumptionPerHour)
                oilConsumption += oilConsumptionPerHour
                functionalRate -= functionalRate*Random.nextDouble(0.0005, 0.05)
                if (functionalRate < 10.0) {
                    BrokenEvent(factory, this, pline!!.priority, listOf(pline!!))
                }
            }
            else -> {}
        }
    }
}
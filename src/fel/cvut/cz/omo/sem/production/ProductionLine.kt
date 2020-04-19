package fel.cvut.cz.omo.sem.production

import fel.cvut.cz.omo.sem.*
import fel.cvut.cz.omo.sem.events.*
import fel.cvut.cz.omo.sem.reports.ConsumptionCounter
import fel.cvut.cz.omo.sem.resources.Material
import fel.cvut.cz.omo.sem.resources.Unit
import fel.cvut.cz.omo.sem.resources.equipment.Equipment
import fel.cvut.cz.omo.sem.resources.equipment.EquipmentApi
import fel.cvut.cz.omo.sem.resources.equipment.EquipmentType
import fel.cvut.cz.omo.sem.resources.people.Person
import kotlin.math.ceil

class ProductionLine(val factory: Factory,
                     val label: String,
                     val priority: LinePriority) : Observer, Visitable, FactoryItem, ConsumptionCounter {

    private var counterOfProductsMade: Double = 0.0
    var productToBeMade: Product? = null
        private set
    private var quantityTarget: Int = 0
    private var state: LineState = LineState.IDLE
    private val productionQueue: MutableList<Pair<Product,Int>> = mutableListOf()
    private val productionSequence: MutableList<Unit> = mutableListOf()
    private val productionSequenceHistory: MutableMap<Int,MutableList<Unit>> = mutableMapOf()
    private var suspendTime = 0
    private val equipmentApi = EquipmentApi()
    override val energyConsumptionHistory: MutableMap<Int, Double> = mutableMapOf()
    override val oilConsumptionHistory: MutableMap<Int, Double> = mutableMapOf()
    override val materialConsumptionHistory: MutableMap<Int, Map<Material, Double>> = mutableMapOf()
    private val productHistory: MutableMap<Int,Product?> = mutableMapOf()

    init {
        factory.addProductionLines(this)
    }

    fun getPeopleInProductionSequence(): List<Person> {
        return productionSequence.filterIsInstance<Person>()
    }

    fun getMachinesInProductionSequence(): List<Equipment> {
       return productionSequence.filterIsInstance<Equipment>().filter { it.type == EquipmentType.MACHINE }
    }

    fun getRobotsInProductionSequence(): List<Equipment> {
        return productionSequence.filterIsInstance<Equipment>().filter { it.type == EquipmentType.ROBOT }
    }

    fun getProductForCycle(cycleNumber: Int): Product? {
        var i = cycleNumber
        while (i >= 0) {
            if (productHistory.containsKey(i)) {
                return productHistory[i]
            }
            i--
        }
        return null
    }

    private fun startProductionOfProduct(product: Product, quantity: Int) {
        if (quantity <= 0) throw IllegalArgumentException("Quantity must be higher than 0!")
        if (!product.assignToProductionLine(this))
            throw IllegalAccessException("Product cannot be produced by more production lines simultaneously.")
        productToBeMade = product
        productHistory[factory.cycleCounter] = product
        counterOfProductsMade = 0.0
        quantityTarget = quantity
        createSequenceAndRun()
    }

    private fun createSequenceAndRun() {
        productionSequence.clear()
        productToBeMade!!.sequence.forEach{
            val units = it.assignUnitsAndGet()
            if (units.isEmpty()) {
                state = LineState.SUSPENDED
                SequenceNotAvailableEvent(factory, it, priority, listOf(this))
                productionSequenceHistory[factory.cycleCounter] = productionSequence
            }
            else {
                productionSequence.addAll(units)
                productionSequenceHistory[factory.cycleCounter] = productionSequence
                units.forEach { u ->
                    val b = u.addToProductionLineSequence(this)
                    if (!b) {
                        state = LineState.SUSPENDED
                        UnitNotAvailableEvent(factory, u, priority, listOf(this))
                    }
                }
            }
            if (state == LineState.IDLE || state == LineState.SUSPENDED) {
                println("Production on line $label started.")
                state = LineState.RUNNING
            }
        }
    }

    fun addToQueue(product: Product, quantity: Int) {
        if (state == LineState.IDLE) startProductionOfProduct(product, quantity)
        else productionQueue.add(Pair(product,quantity))
    }

    fun stopProduction(): Double {
        if (counterOfProductsMade == quantityTarget.toDouble()) {
            println("Production on line $label finished. $counterOfProductsMade products made.")
            productionSequence.forEach { it.removeFromProductionLineSequence(this) }
            productionSequence.clear()
            productionSequenceHistory[factory.cycleCounter] = productionSequence
            productToBeMade!!.removeFromProductionLine(this)
            productToBeMade!!.sequence.forEach { it.clearAssignedUnits() }
            productToBeMade = null
            state = LineState.IDLE
            if (productionQueue.isNotEmpty()) {
                startProductionOfProduct(productionQueue.first().first, productionQueue.first().second)
                productionQueue.removeAt(0)
            }
        }
        else state = LineState.SUSPENDED
        return counterOfProductsMade
    }

    private fun continueProduction() {
        println("Production line $label is producing again.")
        state = LineState.RUNNING
    }

    fun getProductQuantityToReachTarget(): Int {
        return ceil(quantityTarget - counterOfProductsMade).toInt()
    }

    fun getProductionQueue(): List<Pair<Product,Int>> {
        return productionQueue.toList()
    }

    private fun doWork() {
        val product = productToBeMade!!
        product.sequence.forEach { seq ->
            seq.assignUnitsAndGet().forEach { u ->
                u.doWork(seq.material, seq.materialQuantity)
            }
        }
        counterOfProductsMade += product.productsPerCycle

        if (counterOfProductsMade >= quantityTarget.toDouble()) {
            product.make(quantityTarget.toDouble()-counterOfProductsMade+product.productsPerCycle)
            counterOfProductsMade = quantityTarget.toDouble()
            stopProduction()
        }
        else {
            product.make(product.productsPerCycle)
        }
        countConsumption()
    }

    private fun countConsumption() {
        val cycle = factory.cycleCounter
        // energy consumption
        var energy = 0.0
        (getRobotsInProductionSequence()+getMachinesInProductionSequence()).forEach { equipment ->
            energy += equipmentApi.getEnergyConsumption(equipment,cycle)
        }
        energyConsumptionHistory[cycle] = energy
        // oil consumption
        val oil = 0.0
        (getRobotsInProductionSequence()+getMachinesInProductionSequence()).forEach { equipment ->
            energy += equipmentApi.getOilConsumption(equipment,cycle)
        }
        oilConsumptionHistory[cycle] = oil
        // material consumption
        val material = mutableMapOf<Material, Double>()
        (getRobotsInProductionSequence()+getMachinesInProductionSequence()).forEach { equipment ->
            equipmentApi.getMaterialConsumption(equipment,cycle).forEach { materialMap ->
                material[materialMap.key] = material.getOrDefault(materialMap.key, 0.0) + materialMap.value
            }
        }
        (getPeopleInProductionSequence()).forEach { person ->
            person.getMaterialConsumptionForCycle(cycle).forEach { materialMap ->
                material[materialMap.key] = material.getOrDefault(materialMap.key, 0.0) + materialMap.value
            }
        }
        materialConsumptionHistory[cycle] = material.toMap()
    }

    override fun updateState(event: Event) {
        when (event) {
            is UnitAvailableEvent -> {
                createSequenceAndRun()
                factory.outagesReport.addOutageUpdate(factory.cycleCounter,this, event)
            }
            is BrokenEvent, is ChangeBatteryEvent -> {
                stopProduction()
                factory.outagesReport.addNewOutage(factory.cycleCounter,this, event)
            }
            is RepairedEvent, is BatteryChangedEvent -> {
                continueProduction()
                factory.outagesReport.addOutageUpdate(factory.cycleCounter,this, event)
            }
            is RepairStartEvent -> factory.outagesReport.addOutageUpdate(factory.cycleCounter,this, event)
            is SequenceNotAvailableEvent, is UnitNotAvailableEvent ->
                factory.outagesReport.addNewOutage(factory.cycleCounter,this, event)
        }
    }

    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    fun getNumberOfProductsInQueue(): Int {
        return productionQueue.size
    }

    override fun nextCycle() {
        when (state) {
            LineState.RUNNING -> doWork()
            LineState.SUSPENDED -> {
                println("Cycle ${factory.cycleCounter}: production line $label is suspended.")
                suspendTime++
            }
            else -> {}
        }
    }

    fun printSuspendTime() {
        println("Line $label was suspended for $suspendTime cycles.")
    }

    fun getProductionSequenceForCycle(cycleNumber: Int): List<Unit> {
        var i = cycleNumber
        while (i >= 0) {
            if (productionSequenceHistory.containsKey(i)) {
                return productionSequenceHistory[i]!!
            }
            i--
        }
        return listOf()
    }

}
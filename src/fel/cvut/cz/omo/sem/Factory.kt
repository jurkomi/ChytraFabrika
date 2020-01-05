package fel.cvut.cz.omo.sem

import fel.cvut.cz.omo.sem.events.BatteryChangedEvent
import fel.cvut.cz.omo.sem.events.EventContainer
import fel.cvut.cz.omo.sem.production.LinePriority
import fel.cvut.cz.omo.sem.resources.persons.*
import fel.cvut.cz.omo.sem.resources.Material
import fel.cvut.cz.omo.sem.production.Product
import fel.cvut.cz.omo.sem.production.ProductionLine
import fel.cvut.cz.omo.sem.production.SequenceItem
import fel.cvut.cz.omo.sem.resources.equipment.BatteryEquipment
import fel.cvut.cz.omo.sem.resources.equipment.Equipment
import fel.cvut.cz.omo.sem.resources.equipment.EquipmentContainer
import fel.cvut.cz.omo.sem.resources.equipment.PluggedEquipment

class Factory(var name: String) : Visitable, FactoryItem {

    private val products: MutableList<Product> = mutableListOf()
    private val employees: MutableList<Person> = mutableListOf()
    private val materials: MutableList<Material> = mutableListOf()
    private val plines: MutableList<ProductionLine> = mutableListOf()
    private val equipment: MutableList<Equipment> = mutableListOf()
    var tactCounter: Int = 0
        private set

    val eventContainer = EventContainer()
    val employeeContainer = EmployeeContainer()
    val equipmentContainer = EquipmentContainer()

    fun addProduct(vararg productsToAdd: Product) {
        productsToAdd.forEach { if (it.factory == this) products.add(it) else throw IllegalArgumentException("Added product is not part of the factory.")}
    }

    fun addProduct(name: String, productsPerTact: Double, sequence: MutableList<SequenceItem> = mutableListOf()): Product {
        return Product(this, name, productsPerTact, sequence)
    }

    fun addEmployee(vararg employeesToAdd: Person) {
        employeesToAdd.forEach { if (it.factory == this) employees.add(it) else throw IllegalArgumentException("Added unit is not part of the factory.")}
    }

    fun addEmployee(type: String, name: String, purchasePrice: Double = 0.0, salary: Double = 18000.0): Person {
         return when (type.toUpperCase()) {
             "DIRECTOR" -> Director(this, name, purchasePrice, salary)
             "INSPECTOR" -> Inspector(this, name, purchasePrice, salary)
             "REPAIRMAN" -> Repairman(this, name, purchasePrice, salary)
             "WORKMAN" -> Workman(this, name, purchasePrice, salary)
             else -> throw IllegalArgumentException("Allowed person types are: DIRECTOR, INSPECTOR, REPAIRMAN, WORKAMN.")
         }
    }

    fun addEquipment(vararg equipmentToAdd: Equipment) {
        equipmentToAdd.forEach { if (it.factory == this) equipment.add(it) else throw IllegalArgumentException("Added unit is not part of the factory.")}
    }

    fun addEquipment(name: String, purchasePrice: Double, numberOfOperators: Int, energyConsumptionPerHour: Double, oilConsumptionPerHour: Double, batteryCapacity: Double? = null, chargingTimeInHours: Double? = null): Equipment {
        return if (batteryCapacity == null || chargingTimeInHours == null) PluggedEquipment(this, name, purchasePrice, numberOfOperators, energyConsumptionPerHour, oilConsumptionPerHour)
        else BatteryEquipment(this, name, purchasePrice, numberOfOperators, energyConsumptionPerHour, oilConsumptionPerHour, batteryCapacity, chargingTimeInHours)
    }

    fun addMaterials(vararg materialsToAdd: Material) {
        materialsToAdd.forEach { if (it.factory == this) materials.add(it) else throw IllegalArgumentException("Added material is not part of the factory.")}
    }

    fun addMaterial(name: String, purchasePrice: Double): Material {
        return Material(this, name, purchasePrice)
    }

    fun addProductionLines(vararg plinesToAdd: ProductionLine) {
        plinesToAdd.forEach { if (it.factory == this) plines.add(it) else throw IllegalArgumentException("Added production line is not part of the factory.")}
    }

    fun addProductionLine(label: String, priority: LinePriority): ProductionLine {
       return ProductionLine(this, label, priority)
    }

    private fun incrementCounter() {
        tactCounter++
    }

    fun printProducts() {
        println("Products:")
        products.forEach {
            println(it.name)
        }
    }

    fun printName() {
        println("Company name is $name")
    }

    override fun accept(visitor: Visitor) {
        visitor.visit(this)
        when (visitor) {
            is Director -> plines.forEach { it.accept(visitor) }
            is Inspector -> {
                equipment.sortedWith(compareBy {it.functionalRate}).forEach { it.accept(visitor) }
            }
        }
    }

    override fun nextTact() {
        incrementCounter()
        employees.forEach { it.nextTact() }
        plines.forEach { it.nextTact() }
        equipment.forEach { it.nextTact() }
        eventContainer.solveEvents()
    }

    fun simulate(numberOfTactsToSimulate: Int) {
        for(i in 0..numberOfTactsToSimulate) {nextTact()}
    }

}
package fel.cvut.cz.omo.sem

import com.google.gson.*
import com.google.gson.annotations.Expose
import com.google.gson.reflect.TypeToken
import fel.cvut.cz.omo.sem.events.EventContainer
import fel.cvut.cz.omo.sem.production.LinePriority
import fel.cvut.cz.omo.sem.production.Product
import fel.cvut.cz.omo.sem.production.ProductionLine
import fel.cvut.cz.omo.sem.production.SequenceItem
import fel.cvut.cz.omo.sem.reports.ConsumptionReport
import fel.cvut.cz.omo.sem.reports.EventReport
import fel.cvut.cz.omo.sem.reports.FactoryConfigurationReport
import fel.cvut.cz.omo.sem.reports.OutagesReport
import fel.cvut.cz.omo.sem.resources.Material
import fel.cvut.cz.omo.sem.resources.Unit
import fel.cvut.cz.omo.sem.resources.equipment.BatteryEquipment
import fel.cvut.cz.omo.sem.resources.equipment.Equipment
import fel.cvut.cz.omo.sem.resources.equipment.EquipmentContainer
import fel.cvut.cz.omo.sem.resources.equipment.PluggedEquipment
import fel.cvut.cz.omo.sem.resources.people.*
import java.io.File


class Factory(@Expose var name: String) : Visitable, FactoryItem {

    @Expose var oilCost = 200.0
    @Expose var energyCost = 4.3
    @Expose var currency = "CZK"
    @Expose private val materials: MutableList<Material> = mutableListOf()
    @Expose private val employees: MutableList<Person> = mutableListOf()
    @Expose private val equipment: MutableList<Equipment> = mutableListOf()
    @Expose private val products: MutableList<Product> = mutableListOf()
    @Expose private val plines: MutableList<ProductionLine> = mutableListOf()
    var cycleCounter: Int = 0
        private set
    private val plinesHistory: MutableMap<Int,MutableList<ProductionLine>> = mutableMapOf()

    fun getProductionLines(): List<ProductionLine> {
        return plines
    }

    fun getProductionLinesForPeriod(startCycle: Int, endCycle: Int): Map<Int,MutableList<ProductionLine>> {
        if (!plinesHistory.containsKey(startCycle)) {
            var i = startCycle
            while (!plinesHistory.containsKey(i) && i >= 0){
                i--
                if (plinesHistory.containsKey(i)) {
                    plinesHistory[startCycle] = plinesHistory[i]!!
                    break
                }
            }
        }
        if (!plinesHistory.containsKey(startCycle)) plinesHistory[startCycle] = mutableListOf()
        return plinesHistory.filter{ it.key in startCycle..endCycle }
    }

    fun getEquipment(): List<Equipment> {
        return equipment
    }

    val eventContainer = EventContainer(this)
    val employeeContainer = EmployeeContainer()
    val equipmentContainer = EquipmentContainer()
    val outagesReport = OutagesReport(this)
    val consumptionReport = ConsumptionReport(this)
    val eventReport = EventReport(this)
    val factoryConfigurationReport = FactoryConfigurationReport(this)

    fun addProduct(vararg productsToAdd: Product) {
        productsToAdd.forEach {product ->
            if (product.factory == this) {
                products.add(product)
            }
            else throw IllegalArgumentException("Added product is not part of the factory.")
        }
    }

    fun addProduct(name: String, productsPerCycle: Double,
                   sequence: MutableList<SequenceItem> = mutableListOf()): Product {
        if (products.map { it.name }.contains(name))
            throw IllegalArgumentException("Factory already has a product with name $name")
        return Product(this, name, productsPerCycle, sequence)
    }

    fun getProductByName(productName: String): Product? {
        return products.first { it.name == productName }
    }

    fun addEmployee(vararg employeesToAdd: Person) {
        employeesToAdd.forEach {
            if (it.factory == this) employees.add(it)
            else throw IllegalArgumentException("Added unit is not part of the factory.")
        }
    }

    fun addEmployee(type: PersonType, name: String, purchasePrice: Double = 0.0, salary: Double = 18000.0, id: Int? = null): Person {
        if (id != null && employees.map { it.unitId }.contains(id))
            throw IllegalArgumentException("Factory already has an unit with id $id")
         return when (type) {
             PersonType.DIRECTOR -> Director(this, name, purchasePrice, salary, id)
             PersonType.INSPECTOR -> Inspector(this, name, purchasePrice, salary, id)
             PersonType.REPAIRMAN -> Repairman(this, name, purchasePrice, salary, id)
             PersonType.WORKMAN -> Workman(this, name, purchasePrice, salary, id)
         }
    }

    fun getEmployeeById(id: Int): Person? {
        return employees.first { it.unitId == id }
    }

    fun getDirectors(): List<Director> {
        return employees.filter { it.type == PersonType.DIRECTOR}.map { it as Director }
    }

    fun getInspectors(): List<Inspector> {
        return employees.filter { it.type == PersonType.INSPECTOR}.map { it as Inspector }
    }

    fun getUnitById(id: Int): Unit? {
        return if (employees.none { it.unitId == id }) equipment.first { it.unitId == id }
        else employees.first { it.unitId == id }
    }

    fun addEquipment(vararg equipmentToAdd: Equipment) {
        equipmentToAdd.forEach {
            if (it.factory == this) equipment.add(it)
            else throw IllegalArgumentException("Added unit is not part of the factory.")
        }
    }

    fun addEquipment(name: String, purchasePrice: Double, numberOfOperators: Int, energyConsumptionPerHour: Double,
                     oilConsumptionPerHour: Double, batteryCapacity: Double? = null,
                     chargingTimeInHours: Double? = null, id: Int? = null): Equipment {
        return if (batteryCapacity == null || chargingTimeInHours == null)
            PluggedEquipment(this, name, purchasePrice, numberOfOperators, energyConsumptionPerHour,
                oilConsumptionPerHour, id)
        else BatteryEquipment(this, name, purchasePrice, numberOfOperators, energyConsumptionPerHour,
            oilConsumptionPerHour, batteryCapacity, chargingTimeInHours, id)
    }

    fun getEquipmentById(id: Int): Equipment? {
        return equipment.first { it.unitId == id }
    }

    fun addMaterials(vararg materialsToAdd: Material) {
        materialsToAdd.forEach {material ->
            if (material.factory == this) {
                materials.add(material)
            }
            else throw IllegalArgumentException("Added material is not part of the factory.")
        }
    }

    fun addMaterial(name: String, purchasePrice: Double): Material {
        if (materials.map { it.name }.contains(name))
            throw IllegalArgumentException("Factory already has a material with name $name")
        return Material(this, name, purchasePrice)
    }

    fun getMaterialByName(materialName: String): Material? {
        return materials.first { it.name == materialName }
    }

    fun addProductionLines(vararg plinesToAdd: ProductionLine) {
        val lines = plinesToAdd.filter { it.factory == this }.toList()
        plines.addAll(lines)
        plinesHistory[cycleCounter] = plines
    }

    fun addProductionLine(label: String, priority: LinePriority): ProductionLine {
       return ProductionLine(this, label, priority)
    }

    private fun incrementCounter() {
        cycleCounter++
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
    }

    override fun nextCycle() {
        incrementCounter()
        employees.forEach { it.nextCycle() }
        equipment.forEach { it.nextCycle() }
        plines.forEach { it.nextCycle() }
        eventContainer.solveEvents()
    }

    fun simulate(numberOfCyclesToSimulate: Int) {
        for(i in 1..numberOfCyclesToSimulate) {nextCycle()}
    }

    fun saveConfiguration(fileName: String) {
        // convert to json
        val sequenceItemSerializer = JsonSerializer<SequenceItem> { src, _, _ ->
            val jsonSequenceItem = JsonObject()
            jsonSequenceItem.addProperty("itemLabel", src.itemLabel)
            jsonSequenceItem.addProperty("itemType", src.itemType.toString())
            jsonSequenceItem.addProperty("itemQuantity", src.itemQuantity)
            jsonSequenceItem.addProperty("material", src.material.name)
            jsonSequenceItem.addProperty("materialQuantity", src.materialQuantity)
            val assignedUnits = src.getAssignedUnits()
            if (assignedUnits.isNotEmpty()) {
                val jsonArray = JsonArray()
                assignedUnits.forEach { unit ->
                    jsonArray.add(unit.unitId.toString())
                }
                jsonSequenceItem.add("assignedUnitIds", jsonArray)
            }
            jsonSequenceItem
        }
        val productionLineSerializer = JsonSerializer<ProductionLine> { src, _, _ ->
            val jsonPLine = JsonObject()
            jsonPLine.addProperty("label", src.label)
            jsonPLine.addProperty("priority", src.priority.toString())
            if (src.productToBeMade != null) {
                jsonPLine.addProperty("productToBeMade", src.productToBeMade!!.name)
                jsonPLine.addProperty("quantityTarget", src.getProductQuantityToReachTarget())
            }
            val prodQueueList = src.getProductionQueue()
            if (prodQueueList.isNotEmpty()) {
                val jsonProdQueue = JsonArray()
                prodQueueList.forEach { product ->
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("productName", product.first.name)
                    jsonObject.addProperty("quantity", product.second)
                    jsonProdQueue.add(jsonObject)
                }
                jsonPLine.add("productionQueue", jsonProdQueue)
            }
            jsonPLine
        }
        val gson = GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(object : TypeToken<SequenceItem>() {}.type, sequenceItemSerializer)
            .registerTypeAdapter(object : TypeToken<ProductionLine>() {}.type, productionLineSerializer)
            .create()
        val json = gson.toJson(this)

        // save to file
        val name = "$fileName.json"
        val file = File(name)
        if (file.exists()) file.delete()
        file.createNewFile()
        file.writeText(json)
    }

}
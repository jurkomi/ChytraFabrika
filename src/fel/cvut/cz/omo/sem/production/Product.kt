package fel.cvut.cz.omo.sem.production

import com.google.gson.annotations.Expose
import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.Visitable
import fel.cvut.cz.omo.sem.Visitor
import fel.cvut.cz.omo.sem.resources.Material

class Product(val factory: Factory,
              @Expose val name: String,
              @Expose val productsPerCycle: Double,
              @Expose val sequence: MutableList<SequenceItem> = mutableListOf()) : Visitable {

    var quantityMade = 0.0
        private set
    private val materials: MutableMap<Material, Double> = mutableMapOf()
    private var pline: ProductionLine? = null

    init {
        factory.addProduct(this)
        sequence.forEach{item ->
            materials[item.material] =
                materials.getOrDefault(item.material,0.0)+item.materialQuantity*item.itemQuantity
        }
    }

    fun getMaterialsNeeded(): Map<Material, Double> {
        return materials
    }

    fun addWorkmanToSequence(material: Material, materialQuantityPerHour: Double, quantityToAdd: Int = 1) {
        if (quantityToAdd < 1) throw IllegalArgumentException("Quantity must be higher than 0.")
        addToSequence(SequenceItemPerson(factory,quantityToAdd,material,materialQuantityPerHour))
    }

    fun addEquipmentToSequence(material: Material, materialQuantityPerHour: Double, type: String, quantityToAdd: Int = 1) {
        if (quantityToAdd < 1) throw IllegalArgumentException("Quantity must be higher than 0.")
        addToSequence(SequenceItemEquipment(factory,type,quantityToAdd,material,materialQuantityPerHour))
    }

    private fun addToSequence(sequenceItem: SequenceItem) {
        sequence.add(sequenceItem)
        val material = sequenceItem.material
        val materialQuantityPerHour = sequenceItem.materialQuantity
        val quantityToAdd = sequenceItem.itemQuantity
        materials[material] = materials.getOrDefault(material,0.0)+materialQuantityPerHour*quantityToAdd
    }

    fun clearSequence() {
        sequence.clear()
        materials.clear()
    }

    fun make(quantity: Double) {
        quantityMade += quantity
    }

    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    fun assignToProductionLine(productionLine: ProductionLine): Boolean {
        return if (pline == null)  {
            pline = productionLine
            true
        } else false
    }

    fun removeFromProductionLine(productionLine: ProductionLine) {
        if (pline == productionLine) pline = null
    }

}
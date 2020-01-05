package fel.cvut.cz.omo.sem.production

import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.Visitable
import fel.cvut.cz.omo.sem.Visitor
import fel.cvut.cz.omo.sem.resources.Material
import java.lang.Error

class Product(val factory: Factory,
              val name: String,
              val productsPerTact: Double,
              val sequence: MutableList<SequenceItem> = mutableListOf()) : Visitable {

    var quantityMade = 0.0
        private set
    private val materials: MutableMap<Material, Double> = mutableMapOf()

    init {
        factory.addProduct(this)
    }

    fun getMaterialsNeeded(): Map<Material, Double> {
        return materials
    }

    fun addWorkmanToSequence(material: Material, materialQuantityPerHour: Double, quantityToAdd: Int = 1) {
        if (quantityToAdd < 1) throw Error("Quantity must be higher than 0.")
        sequence.add(SequenceItem(factory, SequenceItemType.PERSON,"Workman",quantityToAdd,material,materialQuantityPerHour))
        materials[material] = materials.getOrDefault(material,0.0)+materialQuantityPerHour*quantityToAdd
    }

    fun addEquipmentToSequence(material: Material, materialQuantityPerHour: Double, type: String, quantityToAdd: Int = 1) {
        if (quantityToAdd < 1) throw Error("Quantity must be higher than 0.")
        sequence.add(SequenceItem(factory, SequenceItemType.EQUIPMENT,type,quantityToAdd,material,materialQuantityPerHour))
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

}
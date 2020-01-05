package fel.cvut.cz.omo.sem.resources

import fel.cvut.cz.omo.sem.Factory

class Material(val factory: Factory,
               val name: String,
               val purchasePrice: Double
               //var quantity: Double
) : Resource(factory, name, purchasePrice) {

    var usedTotal = 0.0
        private set

    init {
        factory.addMaterials(this)
    }

/*
    private fun buy(quantity: Double): Double {
        this.quantity += quantity
        return quantity * purchasePrice
    }

    fun checkQuantity(quantity: Double, pline: ProductionLine) {
        if (this.quantity < quantity) {
            buy(quantity - this.quantity)
        }

    }
*/
    fun use(quantity: Double, context: Unit) {
        usedTotal += quantity
        //val cost = purchasePrice*quantity
    }
}
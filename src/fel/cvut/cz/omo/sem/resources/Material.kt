package fel.cvut.cz.omo.sem.resources

import com.google.gson.annotations.Expose
import fel.cvut.cz.omo.sem.Factory

class Material(factory: Factory,
               @Expose val name: String,
               @Expose val purchasePrice: Double
) : Resource(factory, name, purchasePrice) {

    var usedTotal = 0.0
        private set

    init {
        factory.addMaterials(this)
    }

    fun use(quantity: Double, context: Unit) {
        usedTotal += quantity
    }
}
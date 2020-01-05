package fel.cvut.cz.omo.sem.resources.persons

import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.Visitable
import fel.cvut.cz.omo.sem.Visitor
import fel.cvut.cz.omo.sem.production.Product
import fel.cvut.cz.omo.sem.production.ProductionLine
import fel.cvut.cz.omo.sem.resources.Material
import fel.cvut.cz.omo.sem.resources.equipment.Equipment
import java.lang.Error
import kotlin.math.roundToInt

class Inspector(factory: Factory,
                name: String,
                purchasePrice: Double = 0.0,
                salary: Double = 36000.0) : Person(factory, name, purchasePrice, salary),
    Visitor {

    init {
        factory.addEmployee(this)
    }

    override fun doWork(material: Material, quantity: Double) {
        throw Error("Inspector can't do the work.")
    }

    override val visitSequence: MutableList<Pair<Visitable, String>> = mutableListOf()

    override fun visit(visitable: Factory) {
        visitSequence.add(Pair(visitable,"Inspector $name entered factory ${visitable.name}"))
    }

    override fun visit(visitable: ProductionLine) {
        val product = if (visitable.productToBeMade == null) "not producing any product" else "producing product ${visitable.productToBeMade!!.name}"
        visitSequence.add(Pair(visitable,"Production line ${visitable.label} is $product."))
    }

    override fun visit(visitable: Person) {
        visitSequence.add(Pair(visitable,"$name inspecting person ${visitable.name}"))
    }

    override fun visit(visitable: Equipment) {
        visitSequence.add(Pair(visitable,"Wear rate of equipment ${visitable.name} is ${100.0-visitable.functionalRate.roundToInt()}"))
    }

    override fun visit(visitable: Product) {
        visitSequence.add(Pair(visitable,"Number of product ${visitable.name} made in total is ${visitable.quantityMade.roundToInt()}"))
    }

    fun print() {
        visitSequence.forEach {
            println(it.second)
        }
    }
}
package fel.cvut.cz.omo.sem.resources.people

import com.google.gson.annotations.Expose
import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.Visitable
import fel.cvut.cz.omo.sem.Visitor
import fel.cvut.cz.omo.sem.production.Product
import fel.cvut.cz.omo.sem.production.ProductionLine
import fel.cvut.cz.omo.sem.resources.Material
import fel.cvut.cz.omo.sem.resources.equipment.Equipment
import kotlin.math.round

class Director(factory: Factory,
               name: String,
               purchasePrice: Double = 0.0,
               salary: Double = 90000.0,
               id: Int? = null) : Person(factory, name, purchasePrice, salary, id), Visitor {

    @Expose override val type = PersonType.DIRECTOR

    init {
        factory.addEmployee(this)
    }

    override fun doWork(material: Material, quantity: Double) {
        throw IllegalAccessException("Director can't do the work.")
    }

    override val visitSequence: MutableList<Pair<Visitable, String>> = mutableListOf()

    override fun visit(visitable: Factory) {
        visitSequence.add(Pair(visitable,"Director $name entered factory ${visitable.name}"))
        visitable.getProductionLines().forEach { visit(it) }
    }

    override fun visit(visitable: ProductionLine) {
        visitSequence.add(Pair(visitable,"Number of products in queue at production " +
                "line ${visitable.label} is ${visitable.getNumberOfProductsInQueue()}"))
        visitable.getMachinesInProductionSequence().forEach { visit(it)}
        visitable.getRobotsInProductionSequence().forEach { visit(it)}
        visitable.getPeopleInProductionSequence().forEach { visit(it)}
        if (visitable.productToBeMade !=null) visit(visitable.productToBeMade!!)
    }

    override fun visit(visitable: Person) {
        visitSequence.add(Pair(visitable,"Hourly cost of person ${visitable.name} is ${round(visitable.hourlyCost)}"))
    }

    override fun visit(visitable: Equipment) {
        visitSequence.add(Pair(visitable,"Hourly cost of equipment ${visitable.name} is ${round(visitable.hourlyCost)}"))
    }

    override fun visit(visitable: Product) {
        val materialUsagePercentage = mutableMapOf<String, Double>()
        visitable.getMaterialsNeeded().forEach{
            materialUsagePercentage[it.key.name] =
                round((it.value*visitable.quantityMade/visitable.productsPerCycle)/it.key.usedTotal*100)
        }
        visitSequence.add(Pair(visitable,"Percentage of material usage by " +
                "production of product ${visitable.name}: $materialUsagePercentage"))
    }


    fun print() {
        visitSequence.forEach {
            println(it.second)
        }
    }
}
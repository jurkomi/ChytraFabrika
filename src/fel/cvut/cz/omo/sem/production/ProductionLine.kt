package fel.cvut.cz.omo.sem.production

import fel.cvut.cz.omo.sem.*
import fel.cvut.cz.omo.sem.events.*
import fel.cvut.cz.omo.sem.resources.persons.Director
import fel.cvut.cz.omo.sem.resources.Resource
import fel.cvut.cz.omo.sem.resources.Unit

class ProductionLine(val factory: Factory,
                     val label: String,
                     val priority: LinePriority
) : Observer, Visitable, FactoryItem {

    private var counterOfProductsMade: Double = 0.0
    var productToBeMade: Product? = null
        private set
    private var quantityTarget: Int = 0
    private var state: LineState = LineState.IDLE
    private val productionQueue: MutableList<Pair<Product,Int>> = mutableListOf()
    private val problems: MutableCollection<Resource> = mutableSetOf()
    private val productionSequence: MutableList<Unit> = mutableListOf()
    private var suspendTime = 0

    init {
        factory.addProductionLines(this)
    }

    private fun startProductionOfProduct(product: Product, quantity: Int) {
        if (quantity <= 0) throw IllegalArgumentException("Quantity must be higher than 0!")
        productToBeMade = product
        counterOfProductsMade = 0.0
        quantityTarget = quantity
        createSequenceAndRun()
    }

    private fun createSequenceAndRun() {
        productionSequence.clear()
        productToBeMade!!.sequence.forEach{
            val units = it.getAssignedUnits()
            if (units.isEmpty()) {
                state = LineState.SUSPENDED
                SequenceNotAvailableEvent(factory, it, priority, listOf(this))
            }
            else {
                productionSequence.addAll(units)
                units.forEach { u ->
                    val b = u.addToProductionLineSequence(this)
                    if (!b) {
                        UnitNotAvailableEvent(factory, u, priority, listOf(this))
                    }
                }
            }
            if (state == LineState.IDLE) {
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
            productToBeMade!!.sequence.forEach { it.clearAssignedUnits() }
            productToBeMade = null
            state = LineState.IDLE
            if (productionQueue.isNotEmpty()) startProductionOfProduct(productionQueue.first().first,productionQueue.first().second)
        }
        else state = LineState.SUSPENDED
        return counterOfProductsMade
    }

    private fun continueProduction() {
        println("Production line $label is producing again.")
        state = LineState.RUNNING
    }

    private fun doWork() {
        val product = productToBeMade!!
        product.sequence.forEach { seq ->
            seq.getAssignedUnits().forEach { u ->
                u.doWork(seq.material, seq.materialQuantity)
            }
        }
        counterOfProductsMade += product.productsPerTact

        if (counterOfProductsMade >= quantityTarget.toDouble()) {
            product.make(quantityTarget.toDouble()-counterOfProductsMade+product.productsPerTact)
            counterOfProductsMade = quantityTarget.toDouble()
            stopProduction()
        }
        else {
            product.make(product.productsPerTact)
        }
    }

    override fun updateState(event: Event) {
        when (event) {
            is UnitAvailable -> createSequenceAndRun()
            is BrokenEvent -> stopProduction()
            is RepairedEvent -> continueProduction()
        }
    }

    override fun accept(visitor: Visitor) {
        visitor.visit(this)
        if (visitor is Director) {
            productionSequence.forEach { it.accept(visitor)}
            productToBeMade?.accept(visitor)
        }
    }

    fun getNumberOfProductsInQueue(): Int {
        return productionQueue.size
    }

    override fun nextTact() {
        when (state) {
            LineState.RUNNING -> doWork()
            LineState.SUSPENDED -> {
                println("Tact ${factory.tactCounter}: production line $label is suspended.")
                suspendTime++
            }
            else -> {}
        }
    }

    fun printSuspendTime() {
        println("Line $label was suspended for $suspendTime tacts.")
    }

}
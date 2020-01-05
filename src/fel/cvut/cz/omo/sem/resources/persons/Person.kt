package fel.cvut.cz.omo.sem.resources.persons

import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.Visitable
import fel.cvut.cz.omo.sem.Visitor
import fel.cvut.cz.omo.sem.events.Event
import fel.cvut.cz.omo.sem.production.ProductionLine
import fel.cvut.cz.omo.sem.resources.Material
import fel.cvut.cz.omo.sem.resources.Unit

abstract class Person(factory: Factory, name: String, purchasePrice: Double = 0.0, private val salary: Double = 18000.0) : Unit(factory, name, purchasePrice), Visitable {

    override val hourlyCost: Double = salary/180
    var state: PersonState = PersonState.AVAILABLE
        protected set
    var hoursWorked: Int = 0
        private set

    init {
        factory.addEmployee(this)
    }

    fun goHome() {
        state = PersonState.HOME
    }

    fun arriveToJob() {
        state = PersonState.AVAILABLE
    }

    override fun doWork(material: Material, quantity: Double) {
        material.use(quantity, this)
    }

    override fun updateState(event: Event) {
    }

    override fun addToProductionLineSequence(productionLine: ProductionLine): Boolean {
        if (pline == productionLine) return true
        return if (state == PersonState.AVAILABLE) {
            pline = productionLine
            state = PersonState.BUSY
            true
        } else false
    }

    override fun removeFromProductionLineSequence(productionLine: ProductionLine) {
        if (pline == productionLine && state == PersonState.BUSY) {
            state = PersonState.AVAILABLE
            pline = null
        }
        else throw IllegalAccessException("Person does not work in the given production line.")
    }

    override fun isAvailable(): Boolean {
        return (state == PersonState.AVAILABLE)
    }

    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    override fun nextTact() {
        when (state) {
            PersonState.AVAILABLE, PersonState.BUSY -> hoursWorked += 1
            else -> {}
        }
    }
}
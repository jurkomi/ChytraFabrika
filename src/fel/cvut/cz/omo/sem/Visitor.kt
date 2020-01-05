package fel.cvut.cz.omo.sem

import fel.cvut.cz.omo.sem.resources.persons.Person
import fel.cvut.cz.omo.sem.production.Product
import fel.cvut.cz.omo.sem.production.ProductionLine
import fel.cvut.cz.omo.sem.resources.equipment.Equipment

interface Visitor {
    val visitSequence: MutableList<Pair<Visitable, String>>

    fun visit (visitable: Factory)
    fun visit (visitable: ProductionLine)
    fun visit (visitable: Person)
    fun visit (visitable: Equipment)
    fun visit (visitable: Product)
}
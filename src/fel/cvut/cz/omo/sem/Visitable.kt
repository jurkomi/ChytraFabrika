package fel.cvut.cz.omo.sem

interface Visitable {
    fun accept(visitor: Visitor)
}
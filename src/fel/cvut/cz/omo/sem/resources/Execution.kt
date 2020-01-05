package fel.cvut.cz.omo.sem.resources

interface Execution {
    fun doWork(material: Material, quantity: Double)
}
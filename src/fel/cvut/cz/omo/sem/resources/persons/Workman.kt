package fel.cvut.cz.omo.sem.resources.persons

import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.resources.Execution

class Workman(factory: Factory, name: String, purchasePrice: Double = 0.0, salary: Double = 18000.0) : Person(factory, name, purchasePrice, salary), Execution {

    init {
        factory.addEmployee(this)
        factory.employeeContainer.addWorkman(this)
    }

}
package fel.cvut.cz.omo.sem.resources.people

import com.google.gson.annotations.Expose
import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.resources.Execution

class Workman(factory: Factory,
              name: String,
              purchasePrice: Double = 0.0,
              salary: Double = 18000.0,
              id: Int? = null) : Person(factory, name, purchasePrice, salary, id), Execution {

    @Expose override val type = PersonType.WORKMAN

    init {
        factory.addEmployee(this)
        factory.employeeContainer.addWorkman(this)
    }

}
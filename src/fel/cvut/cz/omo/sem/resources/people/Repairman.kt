package fel.cvut.cz.omo.sem.resources.people

import com.google.gson.annotations.Expose
import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.events.Event
import fel.cvut.cz.omo.sem.events.RepairStartEvent
import fel.cvut.cz.omo.sem.events.RepairedEvent
import fel.cvut.cz.omo.sem.resources.Material
import kotlin.random.Random

class Repairman(factory: Factory,
                name: String,
                purchasePrice: Double = 0.0,
                salary: Double = 27000.0,
                id: Int? = null) : Person(factory, name, purchasePrice, salary, id) {

    @Expose override val type = PersonType.REPAIRMAN

    init {
        factory.addEmployee(this)
        factory.employeeContainer.addRepairman(this)
    }

    fun getRepairProgress(): Int {
        return Random.nextInt(20,40)
    }

    override fun doWork(material: Material, quantity: Double) {
        throw IllegalAccessException("Repairman can't do the work.")
    }

    override fun updateState(event: Event) {
        when(event) {
            is RepairStartEvent -> state = PersonState.BUSY
            is RepairedEvent -> state = PersonState.AVAILABLE
        }
    }
}
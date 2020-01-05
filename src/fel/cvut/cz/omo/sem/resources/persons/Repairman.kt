package fel.cvut.cz.omo.sem.resources.persons

import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.events.Event
import fel.cvut.cz.omo.sem.events.RepairStartEvent
import fel.cvut.cz.omo.sem.events.RepairedEvent
import fel.cvut.cz.omo.sem.resources.Material
import java.lang.Error
import kotlin.random.Random

class Repairman(factory: Factory,
                name: String,
                purchasePrice: Double = 0.0,
                salary: Double = 27000.0) : Person(factory, name, purchasePrice, salary) {

    private var counterOfTacts = 0
    private var progress = 0

    init {
        factory.addEmployee(this)
        factory.employeeContainer.addRepairman(this)
    }

    fun getRepairProgress(): Int {
        return Random.nextInt(20,40)
    }

    fun repairEquipment() {
        if (progress == 0) {
            state = PersonState.BUSY
        }
        when(counterOfTacts) {
            0 -> {
                state = PersonState.BUSY
                println("$name Working") //TODO()
            }
            1,2 -> println("$name Working") //TODO()
            else -> {
                state = PersonState.AVAILABLE
                counterOfTacts = 0
                //TODO()
            }
        }
        counterOfTacts++
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun doWork(material: Material, quantity: Double) {
        throw Error("Repairman can't do the work.")
    }

    override fun updateState(event: Event) {
        when(event) {
            is RepairStartEvent -> state = PersonState.BUSY
            is RepairedEvent -> state = PersonState.AVAILABLE
        }
    }
}
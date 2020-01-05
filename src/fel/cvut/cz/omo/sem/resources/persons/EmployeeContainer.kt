package fel.cvut.cz.omo.sem.resources.persons

class EmployeeContainer {

    private val repairmen = mutableListOf<Repairman>()
    private val workmen = mutableListOf<Workman>()

    fun addRepairman(repairman: Repairman) {
        repairmen.add(repairman)
    }

    fun addWorkman(workman: Workman) {
        workmen.add(workman)
    }

    @Synchronized
    fun getRepairman():Repairman? {
        repairmen.forEach {
            if(it.state == PersonState.AVAILABLE) {
                println("${it.name} available")
                return it
            }
        }
        return null
    }

    @Synchronized
    fun getWorkmen(quantity: Int): List<Workman>? {
        val list = workmen.filter {it.state == PersonState.AVAILABLE}
        return if (list.size < quantity) null
            else list.subList(0,quantity)
    }

    @Synchronized
    fun getWorkman(): Workman? {
        return workmen.firstOrNull { it.state == PersonState.AVAILABLE }
    }

    fun shiftChange(shiftEnd: List<Person> = listOf(), shiftStart: List<Person> = listOf()) {
        shiftEnd.forEach {
            it.goHome()
        }
        shiftStart.forEach {
            it.arriveToJob()
        }
    }

}
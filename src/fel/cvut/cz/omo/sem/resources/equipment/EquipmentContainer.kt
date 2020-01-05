package fel.cvut.cz.omo.sem.resources.equipment

import fel.cvut.cz.omo.sem.resources.persons.*

class EquipmentContainer {

    private val equipmentList = mutableListOf<Equipment>()

    fun addEquipment(equipment: Equipment) {
        equipmentList.add(equipment)
    }

    @Synchronized
    fun getOneEquipmentByName(name: String): Equipment? {
        return equipmentList.filter { it.name == name }.firstOrNull { it.isAvailable() }
    }

    @Synchronized
    fun getEquipmentByName(name: String, quantity: Int = 1): List<Equipment>? {
        val list = equipmentList.filter { it.name == name }.filter { it.isAvailable() }
        return if (list.size < quantity) null
        else list.subList(0,quantity)
    }

}
package fel.cvut.cz.omo.sem.deserializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.resources.equipment.BatteryEquipment
import fel.cvut.cz.omo.sem.resources.equipment.Equipment
import fel.cvut.cz.omo.sem.resources.equipment.PluggedEquipment
import fel.cvut.cz.omo.sem.resources.people.*
import java.lang.NullPointerException
import java.lang.reflect.Type

class EmployeeDeserializer(
    val factory: Factory
) : JsonDeserializer<Person> {
    override fun deserialize(p0: JsonElement, p1: Type?, p2: JsonDeserializationContext?): Person {
        val jsonEmployee = p0.asJsonObject
        return when (PersonType.valueOf(jsonEmployee.get("type").asString)) {
            PersonType.DIRECTOR -> Director(factory,
                jsonEmployee.get("name").asString, jsonEmployee.get("purchasePrice").asDouble,
                jsonEmployee.get("salary").asDouble, jsonEmployee.get("unitId").asInt
            )
            PersonType.INSPECTOR -> Inspector(factory,
                jsonEmployee.get("name").asString, jsonEmployee.get("purchasePrice").asDouble,
                jsonEmployee.get("salary").asDouble, jsonEmployee.get("unitId").asInt
            )
            PersonType.REPAIRMAN -> Repairman(factory,
                jsonEmployee.get("name").asString, jsonEmployee.get("purchasePrice").asDouble,
                jsonEmployee.get("salary").asDouble, jsonEmployee.get("unitId").asInt
            )
            PersonType.WORKMAN -> Workman(factory,
                jsonEmployee.get("name").asString, jsonEmployee.get("purchasePrice").asDouble,
                jsonEmployee.get("salary").asDouble, jsonEmployee.get("unitId").asInt
            )
        }
    }
}
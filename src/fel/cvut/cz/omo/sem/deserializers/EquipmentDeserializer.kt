package fel.cvut.cz.omo.sem.deserializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.resources.equipment.BatteryEquipment
import fel.cvut.cz.omo.sem.resources.equipment.Equipment
import fel.cvut.cz.omo.sem.resources.equipment.PluggedEquipment
import java.lang.NullPointerException
import java.lang.reflect.Type

class EquipmentDeserializer(
    val factory: Factory
) : JsonDeserializer<Equipment> {
    override fun deserialize(p0: JsonElement, p1: Type?, p2: JsonDeserializationContext?): Equipment {
        val jsonEquipment = p0.asJsonObject
        if (jsonEquipment.has("batteryCapacity"))
            return BatteryEquipment(factory,
                jsonEquipment.get("name").asString,
                jsonEquipment.get("purchasePrice").asDouble,
                jsonEquipment.get("numberOfOperators").asInt,
                jsonEquipment.get("energyConsumptionPerHour").asDouble,
                jsonEquipment.get("oilConsumptionPerHour").asDouble,
                jsonEquipment.get("batteryCapacity").asDouble,
                jsonEquipment.get("chargingTimeInHours").asDouble,
                jsonEquipment.get("unitId").asInt
            )
        else return PluggedEquipment(factory,
            jsonEquipment.get("name").asString,
            jsonEquipment.get("purchasePrice").asDouble,
            jsonEquipment.get("numberOfOperators").asInt,
            jsonEquipment.get("energyConsumptionPerHour").asDouble,
            jsonEquipment.get("oilConsumptionPerHour").asDouble,
            id = jsonEquipment.get("unitId").asInt
        )
    }
}
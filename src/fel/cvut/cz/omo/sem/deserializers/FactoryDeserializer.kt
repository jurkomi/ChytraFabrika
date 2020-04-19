package fel.cvut.cz.omo.sem.deserializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.production.Product
import fel.cvut.cz.omo.sem.production.SequenceItem
import fel.cvut.cz.omo.sem.production.SequenceItemType
import fel.cvut.cz.omo.sem.resources.Material
import fel.cvut.cz.omo.sem.resources.Resource
import java.lang.NullPointerException
import java.lang.reflect.Type

class FactoryDeserializer : JsonDeserializer<Factory> {

    private var jsonObject: JsonObject? = null

    override fun deserialize(jsonElement: JsonElement, p1: Type?, p2: JsonDeserializationContext?): Factory {
        jsonObject = jsonElement.asJsonObject
        val factory = Factory(jsonObject!!.get("name").asString)
        factory.oilCost = jsonObject!!.get("oilCost").asDouble
        factory.energyCost = jsonObject!!.get("energyCost").asDouble
        factory.currency = jsonObject!!.get("currency").asString
        factory.addMaterials(*getArrayFromJson("materials", MaterialDeserializer(factory)))
        factory.addEmployee(*getArrayFromJson("employees", EmployeeDeserializer(factory)))
        factory.addEquipment(*getArrayFromJson("equipment", EquipmentDeserializer(factory)))
        factory.addProduct(*getArrayFromJson("products", ProductDeserializer(factory)))
        factory.addProductionLines(*getArrayFromJson("plines", ProductionLineDeserializer(factory)))
        return factory
    }

    private inline fun <reified T> getArrayFromJson(memberName: String,
                                                              deserializer: JsonDeserializer<T>): Array<T> {
        if (jsonObject == null) throw NullPointerException()
        return ListFromJson().getFromJson(jsonObject!!.getAsJsonArray(memberName), deserializer).toTypedArray()
    }
}
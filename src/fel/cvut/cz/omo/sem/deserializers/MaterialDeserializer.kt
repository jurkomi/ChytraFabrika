package fel.cvut.cz.omo.sem.deserializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.resources.Material
import java.lang.reflect.Type

class MaterialDeserializer(
    val factory: Factory
) : JsonDeserializer<Material> {
    override fun deserialize(p0: JsonElement, p1: Type?, p2: JsonDeserializationContext?): Material {
        val jsonMaterial = p0.asJsonObject
        return Material(factory, jsonMaterial.get("name").asString, jsonMaterial.get("purchasePrice").asDouble)
    }
}
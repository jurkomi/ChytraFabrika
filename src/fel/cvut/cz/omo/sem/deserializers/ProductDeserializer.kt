package fel.cvut.cz.omo.sem.deserializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.production.Product
import fel.cvut.cz.omo.sem.production.SequenceItem
import fel.cvut.cz.omo.sem.production.SequenceItemType
import fel.cvut.cz.omo.sem.resources.Material
import java.lang.reflect.Type

class ProductDeserializer(
    val factory: Factory
) : JsonDeserializer<Product> {
    override fun deserialize(jsonElement: JsonElement, p1: Type?, p2: JsonDeserializationContext?): Product {
        val jsonProduct = jsonElement.asJsonObject
        val sequence = ListFromJson().getFromJson(jsonProduct.getAsJsonArray("sequence"),
            SequenceItemDeserializer(factory))
        return Product(factory, jsonProduct.get("name").asString, jsonProduct.get("productsPerCycle").asDouble, sequence.toMutableList())
    }
}
package fel.cvut.cz.omo.sem.deserializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.production.*
import fel.cvut.cz.omo.sem.resources.Material
import java.lang.reflect.Type

class ProductionLineDeserializer(
    val factory: Factory
) : JsonDeserializer<ProductionLine> {
    override fun deserialize(jsonElement: JsonElement, p1: Type?, p2: JsonDeserializationContext?): ProductionLine {
        val jsonPLine = jsonElement.asJsonObject
        val pline = ProductionLine(factory,
            jsonPLine.get("label").asString,
            LinePriority.valueOf(jsonPLine.get("priority").asString)
        )
        if (jsonPLine.has("productToBeMade")) {
            val product = factory.getProductByName(jsonPLine.get("productToBeMade").asString)
                ?: throw IllegalArgumentException("Factory does not contain product produced by line.")
            pline.addToQueue(product, jsonPLine.get("quantityTarget").asInt)
        }
        if (jsonPLine.has("productionQueue")) {
            jsonPLine.get("productionQueue").asJsonArray.forEach { jsonQElement ->
                val jsonQueueItem = jsonQElement.asJsonObject
                val product = factory.getProductByName(jsonQueueItem.get("productName").asString)
                    ?: throw IllegalArgumentException("Factory does not contain product produced by line.")
                pline.addToQueue(product, jsonQueueItem.get("quantity").asInt)
            }
        }
        return pline
    }
}
package fel.cvut.cz.omo.sem.deserializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.production.*
import fel.cvut.cz.omo.sem.resources.Material
import java.lang.reflect.Type

class SequenceItemDeserializer(
    val factory: Factory
) : JsonDeserializer<SequenceItem> {
    override fun deserialize(jsonElement: JsonElement, p1: Type?, p2: JsonDeserializationContext?): SequenceItem {
        val jsonSequence = jsonElement.asJsonObject
        val material = factory.getMaterialByName(jsonSequence.get("material").asString)
            ?: throw IllegalArgumentException("Factory does not contain material needed for product.")
        val seqItem =
            SequenceItemFactory().getSequenceItem(factory, SequenceItemType.valueOf(jsonSequence.get("itemType").asString),
                jsonSequence.get("itemLabel").asString, jsonSequence.get("itemQuantity").asInt, material,
                jsonSequence.get("materialQuantity").asDouble)
        if (jsonSequence.has("assignedUnitIds")) {
            jsonSequence.getAsJsonArray("assignedUnitIds").forEach { jsonSeqElement ->
                val jsonId = jsonSeqElement.asJsonPrimitive
                val unit = factory.getUnitById(jsonId.asInt)
                    ?: throw IllegalArgumentException("Factory does not contain unit in the product sequence.")
                seqItem.assignUnit(unit)
            }
        }
        return seqItem
    }
}
package fel.cvut.cz.omo.sem.deserializers

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonDeserializer
import com.google.gson.reflect.TypeToken

class ListFromJson {
    inline fun <reified T> getFromJson(jsonArray: JsonArray, deserializer: JsonDeserializer<T>): List<T> {
        val gson = GsonBuilder()
            .registerTypeAdapter(object : TypeToken<T>() {}.type, deserializer)
            .create()
        val list = mutableListOf<T>()
        jsonArray.forEach { jsonElement ->
            val item = gson.fromJson(jsonElement, T::class.java)
            list.add(item)
        }
        return list
    }
}
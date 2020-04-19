package fel.cvut.cz.omo.sem

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import fel.cvut.cz.omo.sem.deserializers.*
import fel.cvut.cz.omo.sem.production.Product
import fel.cvut.cz.omo.sem.resources.Material
import fel.cvut.cz.omo.sem.resources.Resource
import fel.cvut.cz.omo.sem.resources.equipment.Equipment
import fel.cvut.cz.omo.sem.resources.people.Person
import java.io.File

class SmartFactory {

    private val factories = mutableListOf<Factory>()

    fun saveConfiguration(factory: Factory, fileName: String) {
        factory.saveConfiguration(fileName)
    }

    fun createFactory(name: String): Factory {
        val factory = Factory(name)
        factories.add(factory)
        return factory
    }

    fun getAllFactories(): List<Factory> {
        return factories
    }

    fun createFactoryConfigurationFromJson(fileName: String): Factory {
        val gson = GsonBuilder()
            .registerTypeAdapter(object : TypeToken<Factory>() {}.type, FactoryDeserializer())
            .create()
        val jsonString = readFile(getFileName(fileName))
        return gson.fromJson(jsonString, Factory::class.java)
    }

    private fun getFileName(name: String): String {
        return if (name.length < 6 || name.subSequence(name.length - 5, name.length) != ".json") "$name.json"
        else name
    }

    private fun readFile(fileName: String): String {
        val file = File(fileName)
        if (!file.exists()) throw IllegalArgumentException("File does not exist.")
        return file.readText()
    }
}
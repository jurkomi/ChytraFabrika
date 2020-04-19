package fel.cvut.cz.omo.sem.reports

import fel.cvut.cz.omo.sem.Factory
import java.io.File

abstract class Report(factory: Factory) {

    abstract val defaultFileName: String

    private val reportList = mutableListOf<String>()

    fun addToReport(data: String) {
        reportList.add(data)
    }

    open fun generateReport(startCycleNumber: Int, endCycleNumber: Int, fileName: String = defaultFileName) {
        val name = "$fileName.txt"
        val file = File(name)
        if (file.exists()) file.delete()
        file.createNewFile()
        reportList.forEach {data ->
            file.appendText("$data\n")
        }
    }


}
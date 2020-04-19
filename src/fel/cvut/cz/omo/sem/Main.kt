package fel.cvut.cz.omo.sem

import fel.cvut.cz.omo.sem.reports.FactoryConfigurationReport

fun main() {
    val smartFactory = SmartFactory()
    val firstFactory = smartFactory.createFactoryConfigurationFromJson("configuration1")
    firstFactory.simulate(500)
    val inspector = firstFactory.getInspectors().first()
    inspector.visit(firstFactory)
    inspector.print()
    val director = firstFactory.getDirectors().first()
    director.visit(firstFactory)
    director.print()
}
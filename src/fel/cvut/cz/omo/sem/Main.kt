package fel.cvut.cz.omo.sem

import fel.cvut.cz.omo.sem.resources.persons.*
import fel.cvut.cz.omo.sem.production.LinePriority
import fel.cvut.cz.omo.sem.production.Product

fun main() {
    val f1 = Factory("First Factory")
    val m1 = f1.addMaterial("wood",150.0)
    val p1 = Product(f1,"Wooden doll",  1000.0)
    val p2 = f1.addProduct("Pergola", 0.5)
    val p3 = f1.addProduct( "Rocking horse", 20.0)
    val e1 = f1.addEquipment("eq1",24.5,0,12.1,0.05)
    val e2 = f1.addEquipment("robot",24.5,0,5.0,1.0)
    f1.printName()
    f1.printProducts()
    p3.addWorkmanToSequence(m1,10.0,2)
    p3.addEquipmentToSequence(m1,5.0, "eq1",1)
    p3.addEquipmentToSequence(m1,1.0,"robot",1)
    p3.addWorkmanToSequence(m1,10.0)

    val r1 = f1.addEmployee("REPAIRMAN","rep1")
    val r2 = f1.addEmployee("REPAIRMAN","rep2")
    val r3 = f1.addEmployee("REPAIRMAN","rep3")
    val w1 = f1.addEmployee("WORKMAN","w1")
    val w2 = f1.addEmployee("WORKMAN","w2")
    val w3 = f1.addEmployee("WORKMAN","w3")
    val w4 = f1.addEmployee("WORKMAN","w4")
    val w5 = f1.addEmployee("WORKMAN","w5")
    val w6 = f1.addEmployee("WORKMAN","w6")
    val list = f1.employeeContainer.getWorkman()
    if (list == null) println("null")
    else println(list.name)
    val ee1 = f1.equipmentContainer.getEquipmentByName("eq1")
    if (ee1== null) println("null")
    else println(ee1.first().purchasePrice)
    val ee2 = f1.equipmentContainer.getEquipmentByName("gs")
    if (ee2 == null) println("null")
    else println(ee2.first().name)


    val e4 = f1.addEquipment("1",2.45,2,12.1,216.1)
    val e5 = f1.addEquipment("2",2.45,2,12.1,216.1)
    val e6 = f1.addEquipment("3",2.45,2,12.1,216.1)
    val pl1 = f1.addProductionLine("line1", LinePriority.HIGH)
    println()
    pl1.addToQueue(p3,100)
    pl1.addToQueue(p3,10000)
    pl1.addToQueue(p3,10000)
    e6.doWork(f1.addMaterial("glass",45.1),545.5)
    f1.simulate(260)
    pl1.printSuspendTime()
    val i1 = Inspector(f1, "Petr Stroural")
    f1.accept(i1)
    i1.print()
    val d1 = Director(f1, "Josef Kral")
    f1.accept(d1)
    d1.print()
}
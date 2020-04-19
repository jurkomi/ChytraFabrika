package fel.cvut.cz.omo.sem

import fel.cvut.cz.omo.sem.production.LinePriority
import fel.cvut.cz.omo.sem.production.Product
import fel.cvut.cz.omo.sem.resources.people.Director
import fel.cvut.cz.omo.sem.resources.people.Inspector
import fel.cvut.cz.omo.sem.resources.people.PersonType

fun main() {
    val sf = SmartFactory()
    // factory
    val f1 = sf.createFactory("Second")
    // material
    val flour = f1.addMaterial("flour",15.0)
    val chocolate = f1.addMaterial("chocolate",99.0)
    val oatMilk = f1.addMaterial("oat milk",25.0)
    val caneSugar = f1.addMaterial("cane sugar",79.0)
    val aquafaba = f1.addMaterial("aquafaba",120.0)
    // person
    f1.addEmployee(PersonType.REPAIRMAN,"Karel Novotny")
    f1.addEmployee(PersonType.REPAIRMAN,"Tobias Tichy")
    f1.addEmployee(PersonType.WORKMAN,"Simona Novakova")
    f1.addEmployee(PersonType.WORKMAN,"Alena Svobodova")
    f1.addEmployee(PersonType.WORKMAN,"Eva Dvorakova")
    f1.addEmployee(PersonType.WORKMAN,"David Cerny")
    f1.addEmployee(PersonType.WORKMAN,"Tereza Prochazkova")
    f1.addEmployee(PersonType.WORKMAN,"Jaroslav Pospisil")
    val director = f1.addEmployee(PersonType.DIRECTOR, "Pavel Novak") as Director
    val inspector = f1.addEmployee(PersonType.INSPECTOR, "Anna Vesela") as Inspector
    // equipment
    f1.addEquipment("oven",4699.0,0,1250.0,0.0)
    f1.addEquipment("oven",5199.0,0,1250.0,0.0)
    f1.addEquipment("blender",1259.0,1,821.0,0.0)
    f1.addEquipment("blender",1259.0,1,821.0,0.0)
    f1.addEquipment("blender",1259.0,1,821.0,0.0)
    // product
    val chocolateMilkshake = Product(f1,"Chocolate milkshake", 200.0)
    chocolateMilkshake.addWorkmanToSequence(chocolate, 4.0, 1)
    chocolateMilkshake.addEquipmentToSequence(oatMilk, 40.0, "blender")
    val meringues = f1.addProduct("Meringues", 30.0)
    meringues.addWorkmanToSequence(aquafaba, 3.0, 1)
    meringues.addEquipmentToSequence(caneSugar, 0.7, "blender")
    meringues.addEquipmentToSequence(chocolate, 0.3, "oven")
    val cookies = f1.addProduct( "Cookies", 20.0)
    cookies.addWorkmanToSequence(oatMilk, 3.0, 1)
    cookies.addWorkmanToSequence(flour, 5.0, 1)
    cookies.addEquipmentToSequence(caneSugar, 1.0, "blender")
    cookies.addEquipmentToSequence(chocolate, 1.0, "oven")
    // production line
    val line1 = f1.addProductionLine("line1", LinePriority.HIGH)
    val line2 = f1.addProductionLine("line2", LinePriority.LOW)
    line1.addToQueue(chocolateMilkshake, 10000)
    line1.addToQueue(meringues, 10000)
    line1.addToQueue(chocolateMilkshake, 5000)
    line2.addToQueue(cookies, 20000)
    f1.simulate(50)
    println()
    inspector.visit(f1)
    director.visit(f1)
    inspector.print()
    director.print()
    println()
    f1.simulate(50)
}
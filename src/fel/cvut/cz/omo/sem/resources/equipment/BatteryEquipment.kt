package fel.cvut.cz.omo.sem.resources.equipment

import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.events.ChangeBatteryEvent

class BatteryEquipment(factory: Factory,
                       name: String,
                       purchasePrice: Double,
                       numberOfOperators: Int,
                       energyConsumptionPerHour: Double,
                       oilConsumptionPerHour: Double,
                       private val batteryCapacity: Double,
                       private val chargingTimeInHours: Double
) : Equipment(factory, name, purchasePrice, numberOfOperators, energyConsumptionPerHour, oilConsumptionPerHour) {

    private val chargePerTact: Double = batteryCapacity / chargingTimeInHours
    private var chargeState = batteryCapacity

    init {
        if (batteryCapacity <= 0.0 || chargingTimeInHours <= 0.0) throw IllegalArgumentException("Capacity and charging time have to be bigger than 0.")
        factory.addEquipment(this)
        factory.equipmentContainer.addEquipment(this)
    }


    override fun consumeEnergy(energy: Double) {
        if (chargeState - energy < batteryCapacity*0.25) {
            val event = ChangeBatteryEvent(factory!!,this, pline?.priority)
            if (pline != null) event.register(pline!!)
        }
        else {
            chargeState -= energy
            super.consumeEnergy(energy)
        }
    }

    fun charge(): Boolean {
        chargeState += chargePerTact
        return if (chargeState < batteryCapacity) false
            else {
                chargeState = batteryCapacity
                true
            }
    }

    fun changeBattery() {
        chargeState = batteryCapacity
    }
}
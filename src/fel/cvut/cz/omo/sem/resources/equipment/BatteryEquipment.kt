package fel.cvut.cz.omo.sem.resources.equipment

import com.google.gson.annotations.Expose
import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.events.*

class BatteryEquipment(factory: Factory,
                       name: String,
                       purchasePrice: Double,
                       numberOfOperators: Int,
                       energyConsumptionPerHour: Double,
                       oilConsumptionPerHour: Double,
                       @Expose private val batteryCapacity: Double,
                       @Expose private val chargingTimeInHours: Double,
                       id: Int? = null
) : Equipment(factory, name, purchasePrice, numberOfOperators, energyConsumptionPerHour, oilConsumptionPerHour, id) {

    private val chargePerCycle: Double = batteryCapacity / chargingTimeInHours
    private var chargeState = batteryCapacity

    init {
        if (batteryCapacity <= 0.0 || chargingTimeInHours <= 0.0)
            throw IllegalArgumentException("Capacity and charging time have to be bigger than 0.")
        factory.addEquipment(this)
        factory.equipmentContainer.addEquipment(this)
    }


    override fun consumeEnergy(energy: Double) {
        if (chargeState - energy < batteryCapacity*0.25) {
            val event = ChangeBatteryEvent(factory,this, pline?.priority)
            if (pline != null) event.register(pline!!)
        }
        else {
            chargeState -= energy
            super.consumeEnergy(energy)
        }
    }

    fun charge(): Boolean {
        chargeState += chargePerCycle
        return if (chargeState < batteryCapacity) false
            else {
                chargeState = batteryCapacity
                true
            }
    }

    fun changedBattery() {
        chargeState = batteryCapacity
    }

    override fun updateState(event: Event) {
        if (event is BatteryChangedEvent) changedBattery()
        super.updateState(event)
    }

    override fun returnEquipmentStateToCycle(cycleNumber: Int) {
        chargeState = batteryCapacity
        eventHistory.filter{it.key <= cycleNumber}.values.forEach { list ->
            list.forEach { event ->
                if (event is BatteryChangedEvent) chargeState = batteryCapacity
                when (state) {
                    EquipmentState.STANDBY -> chargeState -= 2.0
                    EquipmentState.OFF -> chargeState -=  0.1
                    EquipmentState.RUNNING -> chargeState -= energyConsumptionPerHour
                    else -> {}
                }
            }
        }
        super.returnEquipmentStateToCycle(cycleNumber)
    }
}
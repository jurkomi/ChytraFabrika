package fel.cvut.cz.omo.sem.events

import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.production.LinePriority
import fel.cvut.cz.omo.sem.Observer

abstract class Event(factory: Factory,
                     context: Observer,
                     linePriority: LinePriority?,
                     registeredUnits: List<Observer>?) {

    abstract val origin: String

    open fun getRepairmanName(): String? {
        return null
    }

    val priority = when (linePriority) {
        LinePriority.HIGH -> EventPriority.IMPORTANT
        LinePriority.MIDDLE -> EventPriority.MODERATE
        LinePriority.LOW -> EventPriority.SLIGHT
        null -> EventPriority.UNIMPORTANT
    }
    protected val registeredUnits: MutableList<Observer> = registeredUnits?.toMutableList() ?: mutableListOf()

    init {
        this.registeredUnits.add(context)
        notifyObservers()
    }

    abstract fun solve(): Boolean
    abstract fun getContext(): Observer

    fun register(observer: Observer) {
        registeredUnits.add(observer)
        observer.updateState(this)
    }

    fun unregister(observer: Observer) {
        registeredUnits.remove(observer)
    }

    private fun notifyObservers() {
        registeredUnits.forEach {
            it.updateState(this)
        }
    }

}
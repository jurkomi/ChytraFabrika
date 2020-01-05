package fel.cvut.cz.omo.sem.events

import fel.cvut.cz.omo.sem.Factory
import fel.cvut.cz.omo.sem.production.LinePriority
import fel.cvut.cz.omo.sem.resources.Unit
import fel.cvut.cz.omo.sem.Observer
import java.util.*
import javax.naming.Context

abstract class Event(private val factory: Factory, private val context: Observer, linePriority: LinePriority?, registeredUnits: List<Observer>?) {

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
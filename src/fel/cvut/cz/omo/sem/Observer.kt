package fel.cvut.cz.omo.sem

import fel.cvut.cz.omo.sem.events.*

interface Observer {
    fun updateState(event: Event)
}
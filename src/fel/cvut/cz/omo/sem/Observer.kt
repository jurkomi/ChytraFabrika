package fel.cvut.cz.omo.sem

import fel.cvut.cz.omo.sem.events.*
import fel.cvut.cz.omo.sem.resources.Resource

interface Observer {
    fun updateState(event: Event)
}
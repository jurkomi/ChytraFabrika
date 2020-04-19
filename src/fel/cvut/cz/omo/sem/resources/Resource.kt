package fel.cvut.cz.omo.sem.resources

import fel.cvut.cz.omo.sem.Factory

abstract class Resource(val factory: Factory,
                        name: String,
                    purchasePrice: Double) {

}
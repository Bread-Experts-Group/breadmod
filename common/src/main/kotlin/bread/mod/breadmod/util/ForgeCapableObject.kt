package bread.mod.breadmod.util

/**
 * Work in progress capability bridge for neoforge.
 */
interface ForgeCapableObject {

    fun addCapability(): Map<CapabilityTypes, Any?>

    // todo figure out how to signal neoforge to invalidate caps on blocks
    //  (maybe use an event in neoforge or some kind of callback that provides pos and level?)
//    fun invalidateCaps() = null
}
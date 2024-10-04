package bread.mod.breadmod.util.forge_handlers

// todo work in progress...
// todo idea: use Architectury's Event class and use it's invoker to call capabilities from common
//  this could also work for calling invalidate caps from the block entity with the event
//  FLUID_CAP_EVENT
//  ENERGY_CAP_EVENT
//  ITEM_CAP_EVENT
//  three separate interface objects and each one is invoked in forge's registration, need to figure out where to call invalidate though
//  look in BlockEntityWithoutLevelRendererEvent for a skeleton example
/**
 * Work in progress capability bridge for neoforge.
 */
interface ForgeCapableObject {

    fun addCapability(): Map<CapabilityTypes, Any?>

    // todo figure out how to signal neoforge to invalidate caps on blocks
    //  (maybe use an event in neoforge or some kind of callback that provides pos and level?)
//    fun invalidateCaps() = null
}
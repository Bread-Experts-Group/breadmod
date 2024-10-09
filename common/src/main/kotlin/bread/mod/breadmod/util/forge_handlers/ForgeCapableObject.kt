package bread.mod.breadmod.util.forge_handlers

// todo work in progress...
//  use @ExpectPlatform to make bridges for registering and manipulating caps in common instead of delegating all the code to NeoForge
/**
 * Work in progress capability bridge for neoforge.
 */
interface ForgeCapableObject {
    fun addCapability(): Map<CapabilityTypes, Any?>
}
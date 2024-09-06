package bread.mod.breadmod

import bread.mod.breadmod.registry.Registry

object ModMainCommon {
    const val MOD_ID: String = "breadmod"

    fun init() {
        // Write common init code here.

        Registry.registerAll()
    }
}

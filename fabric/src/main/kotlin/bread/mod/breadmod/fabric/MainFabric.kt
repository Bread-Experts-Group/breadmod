package bread.mod.breadmod.fabric

import bread.mod.breadmod.Main
import net.fabricmc.api.ModInitializer

class MainFabric : ModInitializer {
    override fun onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.

        Main.init()
    }
}

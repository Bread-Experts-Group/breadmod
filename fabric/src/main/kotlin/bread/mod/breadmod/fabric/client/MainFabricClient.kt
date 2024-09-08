package bread.mod.breadmod.fabric.client

import bread.mod.breadmod.ModMainCommon
import net.fabricmc.api.ClientModInitializer

class MainFabricClient : ClientModInitializer {
    override fun onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        ModMainCommon.initClient()
    }
}

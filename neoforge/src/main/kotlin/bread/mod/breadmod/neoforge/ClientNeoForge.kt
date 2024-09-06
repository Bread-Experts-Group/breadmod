package bread.mod.breadmod.neoforge

import bread.mod.breadmod.ModMainCommon
import net.minecraft.client.gui.screens.Screen
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.client.gui.ConfigurationScreen
import net.neoforged.neoforge.client.gui.IConfigScreenFactory

@Suppress("unused")
@Mod(ModMainCommon.MOD_ID, dist = [Dist.CLIENT])
class ClientNeoForge(container: ModContainer) {
    init {
        container.registerExtensionPoint(
            IConfigScreenFactory::class.java,
            IConfigScreenFactory { mod: ModContainer, parent: Screen -> ConfigurationScreen(mod, parent) }
        )
        println("A")
    }
}

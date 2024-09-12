package bread.mod.breadmod.client.jei

import bread.mod.breadmod.ModMainCommon.modLocation
import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import net.minecraft.resources.ResourceLocation

@JeiPlugin
@Suppress("unused")
class JeiPlugin : IModPlugin {
    override fun getPluginUid(): ResourceLocation = modLocation("jei_plugin")
}
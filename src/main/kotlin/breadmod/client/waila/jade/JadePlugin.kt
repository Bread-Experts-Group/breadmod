package breadmod.client.waila.jade

import breadmod.ModMain
import snownee.jade.api.IWailaClientRegistration
import snownee.jade.api.IWailaCommonRegistration
import snownee.jade.api.IWailaPlugin
import snownee.jade.api.WailaPlugin

@WailaPlugin
class JadePlugin : IWailaPlugin {
    override fun register(registration: IWailaCommonRegistration) {

    }

    override fun registerClient(registration: IWailaClientRegistration) {
        // registration.registerBlockComponent(Test, HeatingElementBlock::class.java)
    }

    companion object {
        val TOOLTIP_RENDERER = ModMain.modLocation("tooltip_renderer") to "Jade Tooltip Renderer"
    }
}
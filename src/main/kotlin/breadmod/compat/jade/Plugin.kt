package breadmod.compat.jade

import breadmod.block.HeatingElementBlock
import breadmod.compat.jade.component.Test
import snownee.jade.api.IWailaClientRegistration
import snownee.jade.api.IWailaCommonRegistration
import snownee.jade.api.IWailaPlugin
import snownee.jade.api.WailaPlugin

@WailaPlugin
@Suppress("unused")
class Plugin: IWailaPlugin {
    override fun register(registration: IWailaCommonRegistration) {
        super.register(registration)
    }

    override fun registerClient(registration: IWailaClientRegistration) {
        registration.registerBlockComponent(Test, HeatingElementBlock::class.java)
    }
}
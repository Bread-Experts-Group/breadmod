package breadmod.compat.jade

import snownee.jade.api.IWailaClientRegistration
import snownee.jade.api.IWailaCommonRegistration
import snownee.jade.api.IWailaPlugin
import snownee.jade.api.WailaPlugin

@WailaPlugin
@Suppress("unused")
class JadePlugin: IWailaPlugin {
    override fun register(registration: IWailaCommonRegistration) {

    }

    override fun registerClient(registration: IWailaClientRegistration) {
        // registration.registerBlockComponent(Test, HeatingElementBlock::class.java)
    }
}
package breadmod.network

import breadmod.BreadMod.modLocation
import net.minecraftforge.network.NetworkRegistry

object PacketHandler {
    private const val PROTOCOL_VERSION = "1"
    val INSTANCE = NetworkRegistry.newSimpleChannel(
        modLocation("main"),
        { PROTOCOL_VERSION },
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    )

    private var idCounter = 0
    init {
        INSTANCE.registerMessage(
            idCounter++, CapabilityDataTransfer::class.java,
            CapabilityDataTransfer::encodeBuf, CapabilityDataTransfer::decodeBuf, CapabilityDataTransfer::handle
        )
    }
}
package breadmod.network

import breadmod.ModMain.LOGGER
import breadmod.ModMain.modLocation
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.simple.SimpleChannel

object PacketHandler {
    private const val PROTOCOL_VERSION = "1"
    val NETWORK: SimpleChannel = NetworkRegistry.newSimpleChannel(
        modLocation("main"),
        { PROTOCOL_VERSION },
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    )

    private var idCounter = 0
    init {
        LOGGER.info("Registering packet message types")
        @Suppress("INACCESSIBLE_TYPE")
        NETWORK.registerMessage(
            idCounter++, CapabilityDataTransfer::class.java,
            CapabilityDataTransfer::encodeBuf, CapabilityDataTransfer::decodeBuf, CapabilityDataTransfer::handle
        )
    }
}
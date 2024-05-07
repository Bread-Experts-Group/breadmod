package breadmod.network

import breadmod.ModMain.LOGGER
import breadmod.ModMain.modLocation
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.simple.SimpleChannel

@Suppress("INACCESSIBLE_TYPE")
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
        NETWORK.registerMessage(
            idCounter++, CapabilityDataPacket::class.java,
            CapabilityDataPacket::encodeBuf, CapabilityDataPacket::decodeBuf, CapabilityDataPacket::handle
        )
        NETWORK.registerMessage(
            idCounter++, VoidTankPacket::class.java,
            VoidTankPacket::encodeBuf, VoidTankPacket::decodeBuf, VoidTankPacket::handle
        )
    }
}
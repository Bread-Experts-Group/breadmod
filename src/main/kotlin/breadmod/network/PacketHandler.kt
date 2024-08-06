package breadmod.network

import breadmod.ModMain.LOGGER
import breadmod.ModMain.modLocation
import breadmod.network.client.BeamPacket
import breadmod.network.client.CapabilitySideDataPacket
import breadmod.network.client.CapabilityTagDataPacket
import breadmod.network.server.ToggleMachinePacket
import breadmod.network.server.VoidTankPacket
import breadmod.network.tool_gun.SDPacket
import breadmod.network.tool_gun.ToolGunCreatorDataPacket
import breadmod.network.tool_gun.ToolGunModeDataPacket
import breadmod.network.tool_gun.ToolGunPacket
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
            idCounter++, CapabilityTagDataPacket::class.java,
            CapabilityTagDataPacket::encodeBuf, CapabilityTagDataPacket::decodeBuf, CapabilityTagDataPacket::handle
        )
        NETWORK.registerMessage(
            idCounter++, CapabilitySideDataPacket::class.java,
            CapabilitySideDataPacket::encodeBuf, CapabilitySideDataPacket::decodeBuf, CapabilitySideDataPacket::handle
        )
        NETWORK.registerMessage(
            idCounter++, VoidTankPacket::class.java,
            VoidTankPacket::encodeBuf, VoidTankPacket::decodeBuf, VoidTankPacket::handle
        )
        NETWORK.registerMessage(
            idCounter++, BeamPacket::class.java,
            BeamPacket::encodeBuf, BeamPacket::decodeBuf, BeamPacket::handle
        )

        NETWORK.registerMessage(
            idCounter++, ToolGunPacket::class.java,
            ToolGunPacket::encodeBuf, ToolGunPacket::decodeBuf, ToolGunPacket::handle
        )
        NETWORK.registerMessage(
            idCounter++, ToolGunModeDataPacket::class.java,
            ToolGunModeDataPacket::encodeBuf, ToolGunModeDataPacket::decodeBuf, ToolGunModeDataPacket::handle
        )
        NETWORK.registerMessage(
            idCounter++, SDPacket::class.java,
            SDPacket::encodeBuf, SDPacket::decodeBuf, SDPacket::handle
        )
        NETWORK.registerMessage(
            idCounter++, ToolGunCreatorDataPacket::class.java,
            ToolGunCreatorDataPacket::encodeBuf, ToolGunCreatorDataPacket::decodeBuf, ToolGunCreatorDataPacket::handle
        )
        NETWORK.registerMessage(
            idCounter++, ToggleMachinePacket::class.java,
            ToggleMachinePacket::encodeBuf, ToggleMachinePacket::decodeBuf, ToggleMachinePacket::handle
        )
    }
}
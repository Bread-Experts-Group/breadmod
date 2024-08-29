package breadmod.network

import breadmod.ModMain.LOGGER
import breadmod.ModMain.modLocation
import breadmod.network.clientbound.BeamPacket
import breadmod.network.clientbound.CapabilitySideDataPacket
import breadmod.network.clientbound.CapabilityTagDataPacket
import breadmod.network.clientbound.tool_gun.ToolGunModeDataPacket
import breadmod.network.common.tool_gun.creator.ToolGunCreatorDataRequestPacket
import breadmod.network.serverbound.ToggleMachinePacket
import breadmod.network.serverbound.VoidTankPacket
import breadmod.network.serverbound.tool_gun.ToolGunConfigurationPacket
import breadmod.network.serverbound.tool_gun.remover.ToolGunRemoverSDPacket
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.simple.SimpleChannel

// internal val logicalServer = Thread.currentThread().threadGroup == SidedThreadGroups.SERVER

@Suppress("INACCESSIBLE_TYPE")
internal object PacketHandler {
    private const val PROTOCOL_VERSION = "1"
    val NETWORK: SimpleChannel = NetworkRegistry.newSimpleChannel(
        modLocation("main"),
        { PROTOCOL_VERSION },
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    )

    init {
        LOGGER.info("Registering clientbound message types")
        // Clientbound //
        NETWORK.registerMessage(
            1_000,
            CapabilityTagDataPacket::class.java,
            CapabilityTagDataPacket::encodeBuf,
            CapabilityTagDataPacket::decodeBuf,
            CapabilityTagDataPacket::handle
        )
        NETWORK.registerMessage(
            1_001,
            CapabilitySideDataPacket::class.java,
            CapabilitySideDataPacket::encodeBuf,
            CapabilitySideDataPacket::decodeBuf,
            CapabilitySideDataPacket::handle
        )
        NETWORK.registerMessage(
            2_000, BeamPacket::class.java,
            BeamPacket::encodeBuf, BeamPacket::decodeBuf, BeamPacket::handle
        )
        NETWORK.registerMessage(
            3_000, ToolGunModeDataPacket::class.java,
            ToolGunModeDataPacket::encodeBuf, ToolGunModeDataPacket::decodeBuf, ToolGunModeDataPacket::handle
        )

        LOGGER.info("Registering serverbound message types")
        // Serverbound //
        NETWORK.registerMessage(
            1_002, VoidTankPacket::class.java,
            VoidTankPacket::encodeBuf, VoidTankPacket::decodeBuf, VoidTankPacket::handle
        )
        NETWORK.registerMessage(
            1_003, ToggleMachinePacket::class.java,
            ToggleMachinePacket::encodeBuf, ToggleMachinePacket::decodeBuf, ToggleMachinePacket::handle
        )
        NETWORK.registerMessage(
            3_001,
            ToolGunConfigurationPacket::class.java,
            ToolGunConfigurationPacket::encodeBuf,
            ToolGunConfigurationPacket::decodeBuf,
            ToolGunConfigurationPacket::handle
        )
        NETWORK.registerMessage(
            3_002, ToolGunRemoverSDPacket::class.java,
            ToolGunRemoverSDPacket::encodeBuf, ToolGunRemoverSDPacket::decodeBuf, ToolGunRemoverSDPacket::handle
        )

        LOGGER.info("Registering common message types")
        NETWORK.registerMessage(
            3_003,
            ToolGunCreatorDataRequestPacket::class.java,
            ToolGunCreatorDataRequestPacket::encodeBuf,
            ToolGunCreatorDataRequestPacket::decodeBuf,
            ToolGunCreatorDataRequestPacket::handle
        )
    }
}
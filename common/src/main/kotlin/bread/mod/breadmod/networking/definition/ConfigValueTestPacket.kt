package bread.mod.breadmod.networking.definition

import bread.mod.breadmod.ModMainCommon.modLocation
import bread.mod.breadmod.registry.config.CommonConfig
import bread.mod.breadmod.registry.config.ConfigValue
import bread.mod.breadmod.util.configValuesMap
import dev.architectury.networking.NetworkManager.NetworkReceiver
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import org.apache.logging.log4j.LogManager

// todo convert to generic solution
internal data class ConfigValueTestPacket(val configValue: ConfigValue<Int>) : CustomPacketPayload {
    companion object {
        val TYPE: CustomPacketPayload.Type<ConfigValueTestPacket> =
            CustomPacketPayload.Type(modLocation("config_value_test"))

        val STREAM_CODEC: StreamCodec<ByteBuf, ConfigValueTestPacket> = StreamCodec.composite(
            ConfigValue.STREAM_CODEC_INT, ConfigValueTestPacket::configValue
        ) { ConfigValueTestPacket(it) }

        val RECEIVER = NetworkReceiver<ConfigValueTestPacket> { (configValue), _ ->
            LogManager.getLogger().info("config value from server: ${configValue.value}")

            // get the old and new value and put them into the map
            configValuesMap[CommonConfig.HAPPY_BLOCK_DIVISIONS] = configValue
            CommonConfig.setConfigValue(CommonConfig.HAPPY_BLOCK_DIVISIONS, configValue.valueOrThrow())
        }
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE
}
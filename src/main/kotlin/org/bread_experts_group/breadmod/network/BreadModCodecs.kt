package org.bread_experts_group.breadmod.network

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.data_holders.ToolGunData
import org.bread_experts_group.breadmod.experimental.particle.ClosedSystem
import java.math.BigDecimal

object BreadModCodecs {
	val EXPANSIBLE_CODEC: Codec<BigDecimal> =
		RecordCodecBuilder.create { instance ->
			instance.group(
				Codec.STRING.fieldOf("value").forGetter(BigDecimal::toEngineeringString)
			).apply(instance, ::BigDecimal)
		}
	val EXPANSIBLE_STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, BigDecimal> = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8,
		BigDecimal::toEngineeringString, ::BigDecimal
	)
	val TOOL_GUN_CODEC: Codec<ToolGunData> =
		RecordCodecBuilder.create { instance ->
			instance.group(
				IToolGunMode.CODEC.fieldOf("mode").forGetter(ToolGunData::mode),
				CompoundTag.CODEC.fieldOf("extra_data").forGetter(ToolGunData::extraData),
				Codec.INT.fieldOf("index").forGetter(ToolGunData::modeIndex)
			).apply(instance, ::ToolGunData)
		}
	val TOOL_GUN_STREAM_CODEC: StreamCodec<FriendlyByteBuf, ToolGunData> = StreamCodec.composite(
		IToolGunMode.STREAM_CODEC, ToolGunData::mode,
		ByteBufCodecs.TRUSTED_COMPOUND_TAG, ToolGunData::extraData,
		ByteBufCodecs.VAR_INT, ToolGunData::modeIndex,
		::ToolGunData
	)
	val CLOSED_SYSTEM_CODEC: Codec<ClosedSystem> =
		RecordCodecBuilder.create { instance ->
			instance.group(
				CompoundTag.CODEC.fieldOf("value").forGetter(ClosedSystem::toNBT)
			).apply(instance, ClosedSystem::createFromTag)
		}
	val CLOSED_SYSTEM_STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ClosedSystem> = StreamCodec.composite(
		ByteBufCodecs.TRUSTED_COMPOUND_TAG,
		ClosedSystem::toNBT, ClosedSystem::createFromTag
	)
	val BLOCK_MAP_STREAM_CODEC: StreamCodec<FriendlyByteBuf, Map<BlockPos, BlockState>> =
		object : StreamCodec<FriendlyByteBuf, Map<BlockPos, BlockState>> {
			private val stateCodec = ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY)
			override fun decode(buffer: FriendlyByteBuf): Map<BlockPos, BlockState> =
				buffer.readMap(BlockPos.STREAM_CODEC, this.stateCodec)

			override fun encode(buffer: FriendlyByteBuf, value: Map<BlockPos, BlockState>) {
				buffer.writeMap(value, BlockPos.STREAM_CODEC, this.stateCodec)
			}
		}
}
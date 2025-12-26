package org.bread_experts_group.breadmod.tool_gun.mode.blueprint

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.core.BlockPos
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.structure.BoundingBox
import org.bread_experts_group.breadmod.network.BreadModCodecs
import org.bread_experts_group.breadmod.util.listOf

data class Blueprint(val blocks: List<Pair<BlockPos, BlockState>>, val bounding: BoundingBox) {
	companion object {
		val CODEC: Codec<Blueprint> = RecordCodecBuilder.create { inst ->
			inst.group(
				BreadModCodecs.kotlinPair(
					BlockPos.CODEC.fieldOf("pos").codec(),
					BlockState.CODEC.fieldOf("state").codec()
				).listOf().fieldOf("blocks").forGetter(Blueprint::blocks),
				BoundingBox.CODEC.fieldOf("bounding").forGetter(Blueprint::bounding)
			).apply(inst, ::Blueprint)
		}
		val STREAM_CODEC: StreamCodec<ByteBuf, Blueprint> = StreamCodec.composite(
			BreadModCodecs.pairStreamCodec(BlockPos.STREAM_CODEC, BreadModCodecs.BLOCKSTATE_STREAM_CODEC).listOf(),
			Blueprint::blocks,
			BreadModCodecs.BOUNDING_BOX_STREAM_CODEC,
			Blueprint::bounding,
			::Blueprint
		)
	}

	override fun toString(): String = "Blueprint[Blocks: ${this.blocks.size}, Bounding: ${this.bounding}"
}
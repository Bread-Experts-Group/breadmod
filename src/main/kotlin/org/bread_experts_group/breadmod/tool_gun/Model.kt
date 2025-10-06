package org.bread_experts_group.breadmod.tool_gun

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.phys.Vec3
import org.bread_experts_group.breadmod.network.BreadModCodecs
import org.joml.Quaternionf

class Model(var state: BlockState, val stateDefinition: StateDefinition<Block, BlockState>) {
	companion object {
		val CODEC: Codec<Model> = RecordCodecBuilder.create { inst ->
			inst.group(
				BlockState.CODEC.fieldOf("state").forGetter(Model::state),
				Codec.FLOAT.fieldOf("scale").forGetter(Model::scale),
				Vec3.CODEC.fieldOf("position").forGetter(Model::position),
				ExtraCodecs.QUATERNIONF.fieldOf("rotation").forGetter(Model::rotation)
			).apply(inst, this::create)
		}
		val STREAM_CODEC: StreamCodec<ByteBuf, Model> = StreamCodec.composite(
			BreadModCodecs.BLOCKSTATE_STREAM_CODEC, Model::state,
			ByteBufCodecs.FLOAT, Model::scale,
			BreadModCodecs.VEC3, Model::position,
			BreadModCodecs.QUATERNIONF, Model::rotation,
			this::create
		)

		private fun create(state: BlockState, scale: Float, pos: Vec3, rotation: Quaternionf): Model {
			val model = Model(state, state.block.stateDefinition)
			model.scale = scale
			model.position = pos
			model.rotation = rotation
			return model
		}
	}

	var scale: Float = 1f
	var position: Vec3 = Vec3.ZERO
	var rotation: Quaternionf = Quaternionf()
	private val originalState: BlockState = this.state
	private var stateIndex: Int = 0
	var isHighlighted: Boolean = false

	fun nextState() {
//			this.state.cycle()
		val states = this.stateDefinition.possibleStates
		val indices = states.indices

		if (this.stateIndex in indices) {
			this.state = states[this.stateIndex++]
		} else {
			this.stateIndex = 0
			this.state = states[this.stateIndex++]
		}
	}

	fun resetState() {
		this.state = this.originalState
	}

	fun move(x: Double, y: Double, z: Double) {
		this.position = this.position.add(x, y, z)
	}
}
package org.bread_experts_group.breadmod.registry.block.actual.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.common.util.INBTSerializable
import net.neoforged.neoforge.energy.IEnergyStorage
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.block.actual.CableBlock
import kotlin.random.Random
import kotlin.reflect.jvm.javaMethod

class CableBlockEntity(
	pos: BlockPos, state: BlockState,
	val capabilities: Map<BlockCapability<*, Direction?>, INBTSerializable<*>> = mapOf()
) : BreadModBlockEntity<CableBlockEntity>(
	ModBlockEntityTypes.CABLE.get(),
	pos,
	state
) {
	val attenuationFactor: Double = 1.2
	val attenuationRandom: Random = Random(-392492)
	val receivedFrom: MutableSet<Direction> = mutableSetOf()
	override fun serverTick(serverLevel: ServerLevel, pos: BlockPos, state: BlockState) {
		// TODO: JUST FOR TESTING [CABLES SET THEIR STATE TO WATERLOGGED ON UPDATE]
		// TODO: REMOVE THIS WHEN A BETTER SOLUTION IS FOUND FOR VISUALIZATION
		val directions = CableBlock.directions.entries.toMutableSet()
		while (directions.isNotEmpty()) {
			val (direction, side) = directions.random().also { directions.remove(it) }
			if (!state.getValue(side.first) || this.receivedFrom.contains(direction)) continue
			val neighborPos = pos.relative(direction)
			for ((capability, cableCapability) in this.capabilities) {
				val neighborCapability = serverLevel.getCapability(
					capability,
					neighborPos,
					direction.opposite
				)
				if (neighborCapability == null) continue
				fun setReceived() {
					val neighborEntity = serverLevel.getBlockEntity(neighborPos)
					if (neighborEntity is CableBlockEntity) {
						neighborEntity.receivedFrom.add(direction.opposite)
						serverLevel.setBlockAndUpdate(
							neighborPos,
							serverLevel.getBlockState(neighborPos).setValue(BlockStateProperties.WATERLOGGED, true)
						)
					}
				}
				when (neighborCapability) {
					is IEnergyStorage -> {
						cableCapability as IEnergyStorage
						if (cableCapability.energyStored == 0) continue
						if (neighborCapability.energyStored >= neighborCapability.maxEnergyStored) continue
						val extracted = cableCapability.extractEnergy(cableCapability.energyStored, false)
						if (extracted == 0) continue
						neighborCapability.receiveEnergy(
							this.attenuationRandom.nextInt(
								(extracted / this.attenuationFactor).toInt(),
								extracted
							),
							false
						)
						setReceived()
					}
					else              -> throw UnsupportedOperationException(
						"capability: ${neighborCapability::class.qualifiedName}"
					)
				}
			}
		}
		this.receivedFrom.clear()
		serverLevel.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.WATERLOGGED, false))
	}

	override fun saveAdditionalBM(tag: CompoundTag, registries: HolderLookup.Provider) {
		for ((capability, cableCapability) in this.capabilities) {
			tag.put(capability.name().path, cableCapability.serializeNBT(registries))
		}
	}

	override fun loadAdditionalBM(tag: CompoundTag, registries: HolderLookup.Provider) {
		for ((capability, cableCapability) in this.capabilities) {
			val tag = tag.get(capability.name().path) ?: continue
			cableCapability::deserializeNBT.javaMethod!!.invoke(cableCapability, registries, tag)
		}
	}
}
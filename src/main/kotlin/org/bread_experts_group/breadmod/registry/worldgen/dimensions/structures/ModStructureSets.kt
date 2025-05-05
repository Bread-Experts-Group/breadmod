package org.bread_experts_group.breadmod.registry.worldgen.dimensions.structures

import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.levelgen.structure.StructureSet
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import kotlin.math.absoluteValue
import kotlin.random.Random

object ModStructureSets {
	fun createKey(id: String): ResourceKey<StructureSet> =
		ResourceKey.create(Registries.STRUCTURE_SET, modLocation(id))

	private val RANDOM: Random = Random(39245)
	private val FARMHOUSE: ResourceKey<StructureSet> = this.createKey("farmhouse")
	fun bootstrap(pContext: BootstrapContext<StructureSet>) {
		val structureHolder = pContext.lookup(Registries.STRUCTURE)
		pContext.register(
			this.FARMHOUSE, StructureSet(
				structureHolder.getOrThrow(ModStructures.FARMHOUSE), RandomSpreadStructurePlacement(
					34, 8, RandomSpreadType.LINEAR, this.RANDOM.nextInt().absoluteValue
				)
			)
		)
	}
}
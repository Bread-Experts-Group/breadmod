package org.bread_experts_group.breadmod.experimental.physics_grid.backend.client

import net.minecraft.client.multiplayer.ClientChunkCache
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.Holder
import net.minecraft.core.RegistryAccess
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import net.minecraft.world.level.dimension.DimensionType
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid

class ClientMicroLevel(
	private val sourceLevel: ClientLevel,
	private val grid: PhysicsGrid
) : ClientLevel(
	null, null, null, null,
	0, 0, null, null, false,
	0
) {
	private val chunkSource: ClientMicroLevelChunkSource = ClientMicroLevelChunkSource(this)
	override fun getChunkSource(): ClientChunkCache = this.chunkSource
	override fun registryAccess(): RegistryAccess = this.sourceLevel.registryAccess()
	override fun dimension(): ResourceKey<Level?> = this.sourceLevel.dimension()
	override fun dimensionType(): DimensionType = this.sourceLevel.dimensionType()
	override fun dimensionTypeRegistration(): Holder<DimensionType?> = this.sourceLevel.dimensionTypeRegistration()
}
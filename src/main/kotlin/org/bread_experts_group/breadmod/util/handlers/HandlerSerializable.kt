package org.bread_experts_group.breadmod.util.handlers

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.neoforged.neoforge.common.util.INBTSerializable

interface HandlerSerializable : HandlerLimits, INBTSerializable<CompoundTag> {
	override fun serializeNBT(registries: HolderLookup.Provider): CompoundTag = CompoundTag()
	override fun deserializeNBT(registries: HolderLookup.Provider, tag: CompoundTag) {}
}
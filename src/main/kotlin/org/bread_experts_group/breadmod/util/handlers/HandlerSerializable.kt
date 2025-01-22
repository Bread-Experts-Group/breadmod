package org.bread_experts_group.breadmod.util.handlers

import net.minecraft.nbt.CompoundTag

interface HandlerSerializable : HandlerLimits {
	fun serializeNBT(): CompoundTag = CompoundTag()
	fun deserializeNBT(tag: CompoundTag) {}
}
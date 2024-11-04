package org.bread_experts_group.breadmod.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes.MONITOR

class BreadScreenBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(MONITOR.get(), pos, state)
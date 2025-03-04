package org.bread_experts_group.breadmod.experimental.computer

import net.minecraft.nbt.CompoundTag
import net.neoforged.neoforge.common.util.INBTSerializable

/**
 * A processor for a Bread Mod computer.
 * @since 1.0.0
 * @see Computer
 * @author Miko Elbrecht
 */
interface Processor : SimulationSteppable, INBTSerializable<CompoundTag> {
	var computer: Computer
}
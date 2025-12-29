package org.bread_experts_group.breadmod.compat.lookingat.jade

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.resources.ResourceLocation
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.datagen.lang.DataGenerateLanguage
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.util.putBigDecimal
import org.bread_experts_group.breadmod.util.putFluid
import snownee.jade.api.BlockAccessor
import snownee.jade.api.IServerDataProvider

object ExtendedHandlerDataProvider : IServerDataProvider<BlockAccessor> {
	@DataGenerateLanguage(name = "Handler Data Provider", prefix = "config.jade.plugin_")
	override fun getUid(): ResourceLocation = modLocation("handler_data_provider")

	override fun appendServerData(data: CompoundTag, accessor: BlockAccessor) {
		val entity = accessor.blockEntity as? BreadModBlockEntity ?: return

		entity.getEnergyHandler()?.let { energy ->
			data.putBigDecimal("bm_energy_stored", energy.bigAmount)
			data.putBigDecimal("bm_energy_capacity", energy.bigCapacity)
		}

		entity.getFluidHandler()?.let { fluid ->
			val tag = ListTag()
			fluid.tanks.forEach { (index, tank) ->
				tag.addTag(index, CompoundTag().also { tankTag ->
					tankTag.putFluid("fluid", tank.fluid)
					tankTag.putBigDecimal("stored", tank.amount)
					tankTag.putBigDecimal("capacity", tank.capacity)
				})
			}
			data.put("bm_fluid_tanks", tag)
		}

		entity.getItemHandler()?.let { item ->
			// todo items
		}
	}

	override fun shouldRequestData(accessor: BlockAccessor): Boolean {
		val entity = accessor.blockEntity as? BreadModBlockEntity ?: return false
		val energy = entity.getEnergyHandler()
		val fluid = entity.getFluidHandler()
		val item = entity.getItemHandler()

		return energy != null || fluid != null || item != null
	}
}
package org.bread_experts_group.breadmod.registry.block

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.experimental.fluid_tank.SidedFluidTankJadeBlockEntity
import org.bread_experts_group.breadmod.registry.block.ModBlocks.asBlock
import org.bread_experts_group.breadmod.registry.block.actual.entity.CableBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.DoubleOrNothingBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.ItemInWorldBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.KeyboardBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.MicrowaveBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.MonitorBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.RadioBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.machine.CreativeGeneratorBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.machine.DieselGeneratorBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.machine.DoughMachineBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.machine.ToasterBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.machine.WheatCrusherBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.storage.EnergyStorageBlockEntity
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.test.FluidEnergyBlockEntity
import java.util.function.Supplier

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
object ModBlockEntityTypes {
	val BLOCK_ENTITY_REGISTRY: DeferredRegister<BlockEntityType<*>> =
		DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, BreadMod.ID)
	val MONITOR: Supplier<BlockEntityType<MonitorBlockEntity>> =
		this.BLOCK_ENTITY_REGISTRY.register("monitor_entity") { ->
			this.buildBlockEntity(::MonitorBlockEntity, ModBlocks.MONITOR.asBlock())
		}
	val KEYBOARD: Supplier<BlockEntityType<KeyboardBlockEntity>> =
		this.BLOCK_ENTITY_REGISTRY.register("keyboard_entity") { ->
			this.buildBlockEntity(::KeyboardBlockEntity, ModBlocks.KEYBOARD.asBlock())
		}
	val WHEAT_CRUSHER: Supplier<BlockEntityType<WheatCrusherBlockEntity>> =
		this.BLOCK_ENTITY_REGISTRY.register("wheat_crusher_entity") { ->
			this.buildBlockEntity(::WheatCrusherBlockEntity, ModBlocks.WHEAT_CRUSHER.asBlock())
		}
	val DOUGH_MACHINE: Supplier<BlockEntityType<DoughMachineBlockEntity>> =
		this.BLOCK_ENTITY_REGISTRY.register("dough_machine_entity") { ->
			this.buildBlockEntity(::DoughMachineBlockEntity, ModBlocks.DOUGH_MACHINE.asBlock())
		}
	val TOASTER: Supplier<BlockEntityType<ToasterBlockEntity>> =
		this.BLOCK_ENTITY_REGISTRY.register("toaster_entity") { ->
			this.buildBlockEntity(::ToasterBlockEntity, ModBlocks.TOASTER.asBlock())
		}
	val MICROWAVE: Supplier<BlockEntityType<MicrowaveBlockEntity>> =
		this.BLOCK_ENTITY_REGISTRY.register("microwave_entity") { ->
			this.buildBlockEntity(::MicrowaveBlockEntity, ModBlocks.MICROWAVE.asBlock())
		}
	val ITEM_IN_WORLD: Supplier<BlockEntityType<ItemInWorldBlockEntity>> =
		this.BLOCK_ENTITY_REGISTRY.register("item_in_world_entity") { ->
			this.buildBlockEntity(::ItemInWorldBlockEntity, ModBlocks.ITEM_IN_WORLD_BLOCK.get())
		}
	val ENERGY_STORAGE: Supplier<BlockEntityType<EnergyStorageBlockEntity>> =
		this.BLOCK_ENTITY_REGISTRY.register("energy_storage_entity") { ->
			this.buildBlockEntity(::EnergyStorageBlockEntity, ModBlocks.ENERGY_STORAGE.asBlock())
		}
	val DOUBLE_OR_NOTHING: Supplier<BlockEntityType<DoubleOrNothingBlockEntity>> =
		this.BLOCK_ENTITY_REGISTRY.register("double_or_nothing_entity") { ->
			this.buildBlockEntity(::DoubleOrNothingBlockEntity, ModBlocks.DOUBLE_OR_NOTHING.asBlock())
		}
	val CREATIVE_GENERATOR: Supplier<BlockEntityType<CreativeGeneratorBlockEntity>> =
		this.BLOCK_ENTITY_REGISTRY.register("creative_generator_entity") { ->
			this.buildBlockEntity(::CreativeGeneratorBlockEntity, ModBlocks.CREATIVE_GENERATOR.asBlock())
		}
	val DIESEL_GENERATOR: Supplier<BlockEntityType<DieselGeneratorBlockEntity>> =
		this.BLOCK_ENTITY_REGISTRY.register("diesel_generator_entity") { ->
			this.buildBlockEntity(::DieselGeneratorBlockEntity, ModBlocks.DIESEL_GENERATOR.asBlock())
		}
	val CABLE: Supplier<BlockEntityType<CableBlockEntity>> =
		this.BLOCK_ENTITY_REGISTRY.register("cable") { ->
			this.buildBlockEntity(::CableBlockEntity, ModBlocks.CABLE.asBlock())
		}
	val RADIO: Supplier<BlockEntityType<RadioBlockEntity>> =
		this.BLOCK_ENTITY_REGISTRY.register("radio") { ->
			this.buildBlockEntity(::RadioBlockEntity, ModBlocks.RADIO_BLOCK.asBlock())
		}

	private fun <T : BlockEntity> buildBlockEntity(
		supplier: BlockEntitySupplier<T>,
		vararg block: Block
	): BlockEntityType<T> = BlockEntityType.Builder.of(supplier, *block).build(null)

	// EXPERIMENTAL PAST THIS POINT
	val FLUID_TANK_JADE_ENTITY: Supplier<BlockEntityType<SidedFluidTankJadeBlockEntity>> =
		this.BLOCK_ENTITY_REGISTRY.register("jade_fluid_tank_entity") { ->
			this.buildBlockEntity(::SidedFluidTankJadeBlockEntity, ModBlocks.JADE_FLUID_TANK.asBlock())
		}
	val FLUID_ENERGY: Supplier<BlockEntityType<FluidEnergyBlockEntity>> =
		this.BLOCK_ENTITY_REGISTRY.register("fluid_energy_entity") { ->
			this.buildBlockEntity(::FluidEnergyBlockEntity, ModBlocks.FLUID_ENERGY.asBlock())
		}
}
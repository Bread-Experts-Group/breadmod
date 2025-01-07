package org.bread_experts_group.breadmod.registry.block

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.test.FluidEnergyBlockEntity
import org.bread_experts_group.breadmod.experimental.fluid_tank.SidedFluidTankJadeBlockEntity
import org.bread_experts_group.breadmod.experimental.recipe.block.multi.fluid.MultiFluidRecipeBlockEntity
import org.bread_experts_group.breadmod.experimental.recipe.block.multi.item.MultiItemRecipeBlockEntity
import org.bread_experts_group.breadmod.experimental.recipe.block.single.fluid.SingleFluidRecipeBlockEntity
import org.bread_experts_group.breadmod.experimental.recipe.block.single.fluid_item.SingleFluidItemRecipeBlockEntity
import org.bread_experts_group.breadmod.experimental.recipe.block.single.item.SingleItemRecipeBlockEntity
import org.bread_experts_group.breadmod.registry.block.ModBlocks.asBlock
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadScreenBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.ItemInWorldBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.MicrowaveBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.SoundBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.machine.DoughMachineBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.machine.ToasterBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.machine.WheatCrusherBlockEntity
import java.util.function.Supplier

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
object ModBlockEntityTypes {
	val BLOCK_ENTITY_REGISTRY : DeferredRegister<BlockEntityType<*>> =
		DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, BreadMod.ID)
	val MONITOR : Supplier<BlockEntityType<BreadScreenBlockEntity>> =
		this.BLOCK_ENTITY_REGISTRY.register("monitor_entity") { ->
			this.buildBlockEntity(::BreadScreenBlockEntity, ModBlocks.MONITOR.asBlock())
		}
	val SOUND_BLOCK : Supplier<BlockEntityType<SoundBlockEntity>> =
		this.BLOCK_ENTITY_REGISTRY.register("sound_block_entity") { ->
			this.buildBlockEntity(::SoundBlockEntity, ModBlocks.SOUND_BLOCK.asBlock())
		}
	val WHEAT_CRUSHER : Supplier<BlockEntityType<WheatCrusherBlockEntity>> =
		this.BLOCK_ENTITY_REGISTRY.register("wheat_crusher_entity") { ->
			this.buildBlockEntity(::WheatCrusherBlockEntity, ModBlocks.WHEAT_CRUSHER.asBlock())
		}
	val DOUGH_MACHINE : Supplier<BlockEntityType<DoughMachineBlockEntity>> =
		this.BLOCK_ENTITY_REGISTRY.register("dough_machine_entity") { ->
			this.buildBlockEntity(::DoughMachineBlockEntity, ModBlocks.DOUGH_MACHINE.asBlock())
		}
	val FLUID_ENERGY : Supplier<BlockEntityType<FluidEnergyBlockEntity>> =
		this.BLOCK_ENTITY_REGISTRY.register("fluid_energy_entity") { ->
			this.buildBlockEntity(::FluidEnergyBlockEntity, ModBlocks.FLUID_ENERGY.asBlock())
		}
	val TOASTER : Supplier<BlockEntityType<ToasterBlockEntity>> =
		this.BLOCK_ENTITY_REGISTRY.register("toaster_entity") { ->
			this.buildBlockEntity(::ToasterBlockEntity, ModBlocks.TOASTER.asBlock())
		}
	val MICROWAVE : Supplier<BlockEntityType<MicrowaveBlockEntity>> =
		this.BLOCK_ENTITY_REGISTRY.register("microwave_entity") { ->
			this.buildBlockEntity(::MicrowaveBlockEntity, ModBlocks.MICROWAVE.asBlock())
		}
	val ITEM_IN_WORLD : Supplier<BlockEntityType<ItemInWorldBlockEntity>> =
		this.BLOCK_ENTITY_REGISTRY.register("item_in_world_entity") { ->
			this.buildBlockEntity(::ItemInWorldBlockEntity, ModBlocks.ITEM_IN_WORLD_BLOCK.asBlock())
		}

	private fun <T : BlockEntity> buildBlockEntity(
		supplier : BlockEntitySupplier<T>,
		vararg block : Block
	) = BlockEntityType.Builder.of(supplier, *block).build(null)
	// EXPERIMENTAL PAST THIS POINT
	val MULTI_ITEM_TEST : Supplier<BlockEntityType<MultiItemRecipeBlockEntity>> =
		this.BLOCK_ENTITY_REGISTRY.register("multi_item_recipe_entity") { ->
			this.buildBlockEntity(::MultiItemRecipeBlockEntity, ModBlocks.MULTI_ITEM_TEST.asBlock())
		}
	val MULTI_FLUID_TEST : Supplier<BlockEntityType<MultiFluidRecipeBlockEntity>> =
		this.BLOCK_ENTITY_REGISTRY.register("multi_fluid_recipe_entity") { ->
			this.buildBlockEntity(::MultiFluidRecipeBlockEntity, ModBlocks.MULTI_FLUID_TEST.asBlock())
		}
	val SINGLE_ITEM_TEST : Supplier<BlockEntityType<SingleItemRecipeBlockEntity>> =
		this.BLOCK_ENTITY_REGISTRY.register("single_item_recipe_entity") { ->
			this.buildBlockEntity(::SingleItemRecipeBlockEntity, ModBlocks.SINGLE_ITEM_TEST.asBlock())
		}
	val SINGLE_FLUID_TEST : Supplier<BlockEntityType<SingleFluidRecipeBlockEntity>> =
		this.BLOCK_ENTITY_REGISTRY.register("single_fluid_recipe_entity") { ->
			this.buildBlockEntity(::SingleFluidRecipeBlockEntity, ModBlocks.SINGLE_FLUID_TEST.asBlock())
		}
	val SINGLE_FLUID_ITEM_TEST : Supplier<BlockEntityType<SingleFluidItemRecipeBlockEntity>> =
		this.BLOCK_ENTITY_REGISTRY.register("single_fluid_item_recipe_entity") { ->
			this.buildBlockEntity(::SingleFluidItemRecipeBlockEntity, ModBlocks.SINGLE_FLUID_ITEM_TEST.asBlock())
		}
	val FLUID_TANK_JADE_ENTITY : Supplier<BlockEntityType<SidedFluidTankJadeBlockEntity>> =
		this.BLOCK_ENTITY_REGISTRY.register("jade_fluid_tank_entity") { ->
			this.buildBlockEntity(::SidedFluidTankJadeBlockEntity, ModBlocks.JADE_FLUID_TANK.asBlock())
		}
}
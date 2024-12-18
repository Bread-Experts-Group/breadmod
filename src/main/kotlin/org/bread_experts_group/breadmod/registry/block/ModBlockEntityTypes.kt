package org.bread_experts_group.breadmod.registry.block

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.experimental.fluid_energy_recipe.FluidEnergyBlockEntity
import org.bread_experts_group.breadmod.experimental.fluid_tank.SidedFluidTankJadeBlockEntity
import org.bread_experts_group.breadmod.experimental.recipe_related.block.multi_fluid.MultiFluidRecipeBlockEntity
import org.bread_experts_group.breadmod.experimental.recipe_related.block.multi_item.MultiItemRecipeBlockEntity
import org.bread_experts_group.breadmod.experimental.recipe_related.block.single_fluid.SingleFluidRecipeBlockEntity
import org.bread_experts_group.breadmod.experimental.recipe_related.block.single_fluid_item.SingleFluidItemRecipeBlockEntity
import org.bread_experts_group.breadmod.experimental.recipe_related.block.single_item.SingleItemRecipeBlockEntity
import org.bread_experts_group.breadmod.registry.block.ModBlocks.asBlock
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadScreenBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.SoundBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.machine.DoughMachineBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.machine.WheatCrusherBlockEntity
import java.util.function.Supplier

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
object ModBlockEntityTypes {
    val BLOCK_ENTITY_REGISTRY: DeferredRegister<BlockEntityType<*>> =
        DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, BreadMod.ID)

    val MONITOR: Supplier<BlockEntityType<BreadScreenBlockEntity>> =
        BLOCK_ENTITY_REGISTRY.register("monitor_entity") { ->
            buildBlockEntity(::BreadScreenBlockEntity, ModBlocks.MONITOR.asBlock())
        }

    val SOUND_BLOCK: Supplier<BlockEntityType<SoundBlockEntity>> =
        BLOCK_ENTITY_REGISTRY.register("sound_block_entity") { ->
            buildBlockEntity(::SoundBlockEntity, ModBlocks.SOUND_BLOCK.asBlock())
        }

    val WHEAT_CRUSHER: Supplier<BlockEntityType<WheatCrusherBlockEntity>> =
        BLOCK_ENTITY_REGISTRY.register("wheat_crusher_entity") { ->
            buildBlockEntity(::WheatCrusherBlockEntity, ModBlocks.WHEAT_CRUSHER.asBlock())
        }

    val DOUGH_MACHINE: Supplier<BlockEntityType<DoughMachineBlockEntity>> =
        BLOCK_ENTITY_REGISTRY.register("dough_machine_entity") { ->
            buildBlockEntity(::DoughMachineBlockEntity, ModBlocks.DOUGH_MACHINE.asBlock())
        }

    val FLUID_ENERGY: Supplier<BlockEntityType<FluidEnergyBlockEntity>> =
        BLOCK_ENTITY_REGISTRY.register("fluid_energy_entity") { ->
            buildBlockEntity(::FluidEnergyBlockEntity, ModBlocks.FLUID_ENERGY.asBlock())
        }

    private fun <T : BlockEntity> buildBlockEntity(
        supplier: BlockEntitySupplier<T>,
        vararg block: Block
    ) = BlockEntityType.Builder.of(supplier, *block).build(null)

    // EXPERIMENTAL PAST THIS POINT

    val MULTI_ITEM_TEST: Supplier<BlockEntityType<MultiItemRecipeBlockEntity>> =
        BLOCK_ENTITY_REGISTRY.register("multi_item_recipe_entity") { ->
            buildBlockEntity(::MultiItemRecipeBlockEntity, ModBlocks.MULTI_ITEM_TEST.asBlock())
        }

    val MULTI_FLUID_TEST: Supplier<BlockEntityType<MultiFluidRecipeBlockEntity>> =
        BLOCK_ENTITY_REGISTRY.register("multi_fluid_recipe_entity") { ->
            buildBlockEntity(::MultiFluidRecipeBlockEntity, ModBlocks.MULTI_FLUID_TEST.asBlock())
        }

    val SINGLE_ITEM_TEST: Supplier<BlockEntityType<SingleItemRecipeBlockEntity>> =
        BLOCK_ENTITY_REGISTRY.register("single_item_recipe_entity") { ->
            buildBlockEntity(::SingleItemRecipeBlockEntity, ModBlocks.SINGLE_ITEM_TEST.asBlock())
        }

    val SINGLE_FLUID_TEST: Supplier<BlockEntityType<SingleFluidRecipeBlockEntity>> =
        BLOCK_ENTITY_REGISTRY.register("single_fluid_recipe_entity") { ->
            buildBlockEntity(::SingleFluidRecipeBlockEntity, ModBlocks.SINGLE_FLUID_TEST.asBlock())
        }

    val SINGLE_FLUID_ITEM_TEST: Supplier<BlockEntityType<SingleFluidItemRecipeBlockEntity>> =
        BLOCK_ENTITY_REGISTRY.register("single_fluid_item_recipe_entity") { ->
            buildBlockEntity(::SingleFluidItemRecipeBlockEntity, ModBlocks.SINGLE_FLUID_ITEM_TEST.asBlock())
        }

    val FLUID_TANK_JADE_ENTITY: Supplier<BlockEntityType<SidedFluidTankJadeBlockEntity>> =
        BLOCK_ENTITY_REGISTRY.register("jade_fluid_tank_entity") { ->
            buildBlockEntity(::SidedFluidTankJadeBlockEntity, ModBlocks.JADE_FLUID_TANK.asBlock())
        }
}
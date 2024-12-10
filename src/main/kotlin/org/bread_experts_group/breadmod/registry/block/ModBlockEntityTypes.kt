package org.bread_experts_group.breadmod.registry.block

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.BreadMod
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

    private fun <T : BlockEntity> registerBlockEntity(
        name: String, supplier: BlockEntitySupplier<T>,
        vararg block: Block
    ) = BLOCK_ENTITY_REGISTRY.register(name) { -> BlockEntityType.Builder.of(supplier, *block).build(null) }

    val MONITOR: Supplier<BlockEntityType<BreadScreenBlockEntity>> =
        registerBlockEntity("monitor_entity", ::BreadScreenBlockEntity, ModBlocks.MONITOR.asBlock())

    val SOUND_BLOCK: Supplier<BlockEntityType<SoundBlockEntity>> =
        registerBlockEntity("sound_block_entity", ::SoundBlockEntity, ModBlocks.SOUND_BLOCK.asBlock())

    val WHEAT_CRUSHER: Supplier<BlockEntityType<WheatCrusherBlockEntity>> =
        registerBlockEntity("wheat_crusher_entity", ::WheatCrusherBlockEntity, ModBlocks.WHEAT_CRUSHER.asBlock())

    val DOUGH_MACHINE: Supplier<BlockEntityType<DoughMachineBlockEntity>> =
        registerBlockEntity("dough_machine_entity", ::DoughMachineBlockEntity, ModBlocks.DOUGH_MACHINE.asBlock())

    // EXPERIMENTAL PAST THIS POINT

    val MULTI_ITEM_TEST: Supplier<BlockEntityType<MultiItemRecipeBlockEntity>> = registerBlockEntity(
        "multi_item_recipe_entity",
        ::MultiItemRecipeBlockEntity,
        ModBlocks.MULTI_ITEM_TEST.asBlock()
    )

    val MULTI_FLUID_TEST: Supplier<BlockEntityType<MultiFluidRecipeBlockEntity>> = registerBlockEntity(
        "multi_fluid_recipe_entity",
        ::MultiFluidRecipeBlockEntity,
        ModBlocks.MULTI_FLUID_TEST.asBlock()
    )

    val SINGLE_ITEM_TEST: Supplier<BlockEntityType<SingleItemRecipeBlockEntity>> = registerBlockEntity(
        "single_item_recipe_entity",
        ::SingleItemRecipeBlockEntity,
        ModBlocks.SINGLE_ITEM_TEST.asBlock()
    )

    val SINGLE_FLUID_TEST: Supplier<BlockEntityType<SingleFluidRecipeBlockEntity>> = registerBlockEntity(
        "single_fluid_recipe_entity",
        ::SingleFluidRecipeBlockEntity,
        ModBlocks.SINGLE_FLUID_TEST.asBlock()
    )

    val SINGLE_FLUID_ITEM_TEST: Supplier<BlockEntityType<SingleFluidItemRecipeBlockEntity>> = registerBlockEntity(
        "single_fluid_item_recipe_entity",
        ::SingleFluidItemRecipeBlockEntity,
        ModBlocks.SINGLE_FLUID_ITEM_TEST.asBlock()
    )
}
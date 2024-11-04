package org.bread_experts_group.breadmod.registry.block

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.Breadmod
import org.bread_experts_group.breadmod.block.entity.BreadScreenBlockEntity
import org.bread_experts_group.breadmod.block.entity.SoundBlockEntity
import org.bread_experts_group.breadmod.block.entity.machine.WheatCrusherBlockEntity
import org.bread_experts_group.breadmod.registry.block.ModBlocks.asBlock
import java.util.function.Supplier

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
object ModBlockEntityTypes {
    val BLOCK_ENTITY_REGISTRY: DeferredRegister<BlockEntityType<*>> =
        DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Breadmod.ID)

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

    /* Caused by: java.lang.NullPointerException: Trying to access unbound value: ResourceKey[minecraft:item / breadmod:monitor] */
/*    fun <T : BlockEntity> DeferredRegister<BlockEntityType<*>>.registerBlockEntity(
        id: String,
        supplier: BlockEntitySupplier<T>,
        block: Block
    ): Supplier<BlockEntityType<out T>> = this.register(id) { ->
        BlockEntityType.Builder.of(
            supplier,
            block
        ).build(Util.fetchChoiceType(References.BLOCK_ENTITY, id))
    }*/

    private fun <T : BlockEntity> buildBlockEntity(
        supplier: BlockEntitySupplier<T>,
        vararg block: Block
    ) = BlockEntityType.Builder.of(supplier, *block).build(null)
}
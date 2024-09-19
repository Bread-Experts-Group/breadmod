package bread.mod.breadmod.registry.block

import bread.mod.breadmod.ModMainCommon
import bread.mod.breadmod.block.entity.SoundBlockEntity
import bread.mod.breadmod.block.entity.ToasterBlockEntity
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.entity.BlockEntityType

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
object ModBlockEntityTypes {
    val BLOCK_ENTITY_TYPE_REGISTRY: DeferredRegister<BlockEntityType<*>> =
        DeferredRegister.create(ModMainCommon.MOD_ID, Registries.BLOCK_ENTITY_TYPE)

    val SOUND_BLOCK: RegistrySupplier<BlockEntityType<SoundBlockEntity>> =
        BLOCK_ENTITY_TYPE_REGISTRY.register("sound_block_entity") {
            BlockEntityType.Builder.of(
                { pos, state -> SoundBlockEntity(pos, state) },
                ModBlocks.SOUND_BLOCK.get().block
            ).build(null)
        }

    val TOASTER: RegistrySupplier<BlockEntityType<ToasterBlockEntity>> =
        BLOCK_ENTITY_TYPE_REGISTRY.register("toaster_block_entity") {
            BlockEntityType.Builder.of(
                { pos, state -> ToasterBlockEntity(pos, state) },
                ModBlocks.TOASTER.get().block
            ).build(null)
        }
}
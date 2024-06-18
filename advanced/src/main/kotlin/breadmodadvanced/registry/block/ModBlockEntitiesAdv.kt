package breadmodadvanced.registry.block

import breadmodadvanced.ModMainAdv
import breadmodadvanced.block.entity.DieselGeneratorBlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModBlockEntitiesAdv {
    val deferredRegister: DeferredRegister<BlockEntityType<*>> = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ModMainAdv.ID)

    val DIESEL_GENERATOR: RegistryObject<BlockEntityType<DieselGeneratorBlockEntity>> = deferredRegister.register("diesel_generator_entity") {
        BlockEntityType.Builder.of(
            { pPos, pState -> DieselGeneratorBlockEntity(pPos, pState) },
            ModBlocksAdv.DIESEL_GENERATOR.get().block
        ).build(null)
    }
}
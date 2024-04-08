package breadmod.block.registry

import breadmod.BreadMod
import breadmod.block.BreadFurnaceBlock
import breadmod.block.HeatingElementBlock
import breadmod.block.registry.ModBlocks.BREAD_FURNACE_BLOCK
import breadmod.block.registry.ModBlocks.HEATING_ELEMENT_BLOCK
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModBlockEntities {
    val REGISTRY: DeferredRegister<BlockEntityType<*>> = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, BreadMod.ID)
    val BREAD_FURNACE: RegistryObject<BlockEntityType<BreadFurnaceBlock.BlockEntity>> = REGISTRY.register("bread_furnace_entity") {
        BlockEntityType.Builder.of(
            { pPos, pState -> BreadFurnaceBlock.BlockEntity(pPos, pState) },
            BREAD_FURNACE_BLOCK.get().block
        ).build(null)
    }
    val HEATING_ELEMENT: RegistryObject<BlockEntityType<HeatingElementBlock.HEBlockEntity>> = REGISTRY.register("heating_element_entity") {
        BlockEntityType.Builder.of(
            { pPos, pState -> HeatingElementBlock.HEBlockEntity(pPos, pState) },
            HEATING_ELEMENT_BLOCK.get().block
        ).build(null)
    }
}
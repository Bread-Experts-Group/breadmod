package breadmod.registry.block

import breadmod.BreadMod
import breadmod.block.entity.BreadFurnaceBlockEntity
import breadmod.block.entity.BreadScreenBlockEntity
import breadmod.block.entity.HeatingElementBlockEntity
import breadmod.registry.block.ModBlocks.BREAD_FURNACE_BLOCK
import breadmod.registry.block.ModBlocks.HEATING_ELEMENT_BLOCK
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModBlockEntities {
    val deferredRegister: DeferredRegister<BlockEntityType<*>> = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, BreadMod.ID)

    val BREAD_FURNACE: RegistryObject<BlockEntityType<BreadFurnaceBlockEntity>> = deferredRegister.register("bread_furnace_entity") {
        BlockEntityType.Builder.of(
            { pPos, pState -> BreadFurnaceBlockEntity(pPos, pState) },
            BREAD_FURNACE_BLOCK.get().block
        ).build(null)
    }
    val HEATING_ELEMENT: RegistryObject<BlockEntityType<HeatingElementBlockEntity>> = deferredRegister.register("heating_element_entity") {
        BlockEntityType.Builder.of(
            { pPos, pState -> HeatingElementBlockEntity(pPos, pState) },
            HEATING_ELEMENT_BLOCK.get().block
        ).build(null)
    }
    val BREAD_SCREEN: RegistryObject<BlockEntityType<BreadScreenBlockEntity>> = deferredRegister.register("bread_screen_entity") {
        BlockEntityType.Builder.of(
            { pPos, pState -> BreadScreenBlockEntity(pPos, pState) },
            ModBlocks.BREAD_SCREEN.get().block
        ).build(null)
    }
}
package breadmod.registry.block

import breadmod.ModMain
import breadmod.block.entity.BreadScreenBlockEntity
import breadmod.block.entity.DoughMachineBlockEntity
import breadmod.block.entity.HeatingElementBlockEntity
import breadmod.block.entity.WheatCrusherBlockEntity
import breadmod.registry.block.ModBlocks.DOUGH_MACHINE_BLOCK
import breadmod.registry.block.ModBlocks.HEATING_ELEMENT_BLOCK
import breadmod.registry.block.ModBlocks.WHEAT_CRUSHER_BLOCK
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModBlockEntities {
    val deferredRegister: DeferredRegister<BlockEntityType<*>> = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ModMain.ID)

    val DOUGH_MACHINE: RegistryObject<BlockEntityType<DoughMachineBlockEntity>> = deferredRegister.register("dough_machine_entity") {
        BlockEntityType.Builder.of(
            { pPos, pState -> DoughMachineBlockEntity(pPos, pState) },
            DOUGH_MACHINE_BLOCK.get().block
        ).build(null)
    }
    val WHEAT_CRUSHER: RegistryObject<BlockEntityType<WheatCrusherBlockEntity>> = deferredRegister.register("wheat_crusher_entity") {
        BlockEntityType.Builder.of(
            { pPos, pState -> WheatCrusherBlockEntity(pPos, pState) },
            WHEAT_CRUSHER_BLOCK.get().block
        ).build(null)
    }
    val HEATING_ELEMENT: RegistryObject<BlockEntityType<HeatingElementBlockEntity>> = deferredRegister.register("heating_element_entity") {
        BlockEntityType.Builder.of(
            { pPos, pState -> HeatingElementBlockEntity(pPos, pState) },
            HEATING_ELEMENT_BLOCK.get().block
        ).build(null)
    }
    val MONITOR: RegistryObject<BlockEntityType<BreadScreenBlockEntity>> = deferredRegister.register("monitor_entity") {
        BlockEntityType.Builder.of(
            { pPos, pState -> BreadScreenBlockEntity(pPos, pState) },
            ModBlocks.MONITOR.get().block
        ).build(null)
    }
}
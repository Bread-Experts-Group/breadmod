package breadmod.registry.block

import breadmod.ModMain
import breadmod.block.entity.*
import breadmod.block.multiblock.farmer.entity.*
import breadmod.registry.block.ModBlocks.DOUGH_MACHINE_BLOCK
import breadmod.registry.block.ModBlocks.HEATING_ELEMENT_BLOCK
import breadmod.registry.block.ModBlocks.WHEAT_CRUSHER_BLOCK
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
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

    val MULTIBLOCK_GENERIC_POWER: RegistryObject<BlockEntityType<PowerInterfaceBlockEntity>> = deferredRegister.register("generic_power_entity") {
        BlockEntityType.Builder.of(
            { pPos, pState -> PowerInterfaceBlockEntity(pPos, pState) },
            ModBlocks.GENERIC_POWER_INTERFACE.get().block
        ).build(null)
    }

    // Farmer
    val FARMER_INPUT: RegistryObject<BlockEntityType<FarmerInputBlockEntity>> = deferredRegister.register("farmer_input_entity") {
        BlockEntityType.Builder.of(
            { pPos, pState -> FarmerInputBlockEntity(pPos, pState) },
            ModBlocks.FARMER_INPUT_BLOCK.get().block
        ).build(null)
    }
    val FARMER_OUTPUT: RegistryObject<BlockEntityType<FarmerOutputBlockEntity>> = deferredRegister.register("farmer_output_entity") {
        BlockEntityType.Builder.of(
            { pPos, pState -> FarmerOutputBlockEntity(pPos, pState) },
            ModBlocks.FARMER_OUTPUT_BLOCK.get().block
        ).build(null)
    }
    val FARMER_CONTROLLER: RegistryObject<BlockEntityType<FarmerControllerBlockEntity>> = deferredRegister.register("farmer_controller_entity") {
        BlockEntityType.Builder.of(
            { pPos, pState -> FarmerControllerBlockEntity(pPos, pState) },
            ModBlocks.FARMER_CONTROLLER.get().block
        ).build(null)
    }

    // Power Generators
    val COAL_GENERATOR: RegistryObject<BlockEntityType<CoalGeneratorBlockEntity>> = deferredRegister.register("coal_generator_entity") {
        BlockEntityType.Builder.of(
            { pPos, pState -> CoalGeneratorBlockEntity(pPos, pState) },
            ModBlocks.COAL_GENERATOR.get().block
        ).build(null)
    }
}
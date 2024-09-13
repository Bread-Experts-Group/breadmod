package bread.mod.breadmod.neoforge

import bread.mod.breadmod.ModMainCommon
import bread.mod.breadmod.datagen.recipe.SmartRecipeProvider
import bread.mod.breadmod.datagen.tag.SmartTagProvider
import bread.mod.breadmod.neoforge.datagen.SmartBlockModelProviderNeoForge
import bread.mod.breadmod.neoforge.datagen.SmartItemModelProviderNeoForge
import bread.mod.breadmod.neoforge.datagen.SmartLanguageProviderNeoForge
import bread.mod.breadmod.neoforge.datagen.SmartSoundProviderNeoForge
import bread.mod.breadmod.neoforge.util.EnergyStorageWrapper
import bread.mod.breadmod.neoforge.util.FluidStackWrapper
import bread.mod.breadmod.registry.block.ModBlockEntityTypes
import bread.mod.breadmod.registry.block.ModBlocks
import net.minecraft.world.inventory.CraftingContainer
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.data.event.GatherDataEvent
import net.neoforged.neoforge.items.wrapper.InvWrapper

@Suppress("unused")
@EventBusSubscriber(modid = ModMainCommon.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
internal object CommonModEventBus {
    @SubscribeEvent
    fun gatherData(event: GatherDataEvent) {
        SmartLanguageProviderNeoForge(
            ModMainCommon.MOD_ID,
            ModMainCommon::class.java.classLoader, ModMainCommon::class.java.`package`
        ).generate(event)

        SmartBlockModelProviderNeoForge(
            ModMainCommon.MOD_ID,
            ModMainCommon::class.java.classLoader, ModMainCommon::class.java.`package`
        ).generate(event)

        SmartItemModelProviderNeoForge(
            ModMainCommon.MOD_ID,
            ModMainCommon::class.java.classLoader, ModMainCommon::class.java.`package`
        ).generate(event)

        SmartSoundProviderNeoForge(
            ModMainCommon.MOD_ID,
            ModMainCommon::class.java.classLoader, ModMainCommon::class.java.`package`
        ).generate(event)

        SmartTagProvider(
            ModMainCommon.MOD_ID,
            ModMainCommon::class.java.classLoader, ModMainCommon::class.java.`package`
        ).getProvider(event.generator.packOutput, event.lookupProvider).forEach {
            event.generator.addProvider(true, it)
        }

        event.generator.addProvider(
            true, SmartRecipeProvider(
                ModMainCommon.MOD_ID,
                ModMainCommon::class.java.classLoader, ModMainCommon::class.java.`package`
            ).getProvider(event.generator.packOutput, event.lookupProvider)
        )
    }

    @SubscribeEvent
    fun registerCaps(event: RegisterCapabilitiesEvent) {
        event.registerBlock(
            Capabilities.ItemHandler.BLOCK,
            { level, pos, state, blockEntity, side ->
                InvWrapper(ModBlockEntityTypes.TOASTER.get().getBlockEntity(level, pos) as CraftingContainer)
            }, ModBlocks.TOASTER.get().block
        )

        event.registerBlock(
            Capabilities.EnergyStorage.BLOCK,
            { level, pos, state, blockEntity, side ->
                EnergyStorageWrapper(level, pos)
            }, ModBlocks.TOASTER.get().block
        )

        event.registerBlock(
            Capabilities.FluidHandler.BLOCK,
            { level, pos, state, blockEntity, side ->
                FluidStackWrapper(level, pos)
            }, ModBlocks.TOASTER.get().block
        )
    }
}
package bread.mod.breadmod.datagen.loot

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import java.util.concurrent.CompletableFuture

fun constructLootProvider(
    blockLootProvider: BlockLootSubProvider,
    output: PackOutput,
    registries: CompletableFuture<HolderLookup.Provider>
): LootTableProvider {
    return LootTableProvider(
        output,
        setOf(),
        listOf(
            SubProviderEntry(
                { blockLootProvider },
                LootContextParamSets.BLOCK
            )
        ),
        registries
    )
}
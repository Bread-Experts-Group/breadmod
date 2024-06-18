package breadmod.datagen

import net.minecraft.data.PackOutput
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets

fun constructLootProvider(blockLootProvider: BlockLootSubProvider, output: PackOutput): LootTableProvider {
    return LootTableProvider(
        output, setOf(), listOf(SubProviderEntry({ blockLootProvider }, LootContextParamSets.BLOCK))
    )
}
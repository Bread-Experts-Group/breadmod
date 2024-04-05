package breadmod.datagen

import breadmod.block.registry.ModBlocks
import net.minecraft.data.PackOutput
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets

object ModLootTableProvider {
    fun create(output: PackOutput): LootTableProvider {
        return LootTableProvider(
            output, setOf(), listOf(SubProviderEntry({ ModBlocks.ModBlockLoot() }, LootContextParamSets.BLOCK))
        )
    }
}
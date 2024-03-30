package breadmod.datagen

import breadmod.block.ModBlocks
import net.minecraft.data.PackOutput
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets

object ModLootTableProvider {
    fun create(output: PackOutput?): LootTableProvider? {
        return output?.let {
            LootTableProvider(
                it, setOf(), listOf(
                    SubProviderEntry({ ModBlocks.ModBlockLoot() }, LootContextParamSets.BLOCK)
                )
            )
        }
    }
}
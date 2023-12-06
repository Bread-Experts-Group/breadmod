package breadmod.datagen.provider.loot

import breadmod.block.ModBlocks
import com.mojang.datafixers.util.Pair
import net.minecraft.data.DataGenerator
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.LootTables
import net.minecraft.world.level.storage.loot.ValidationContext
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Supplier

typealias LootSupplier = Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>
class ModLootTables(generator: DataGenerator) : LootTableProvider(generator) {
    override fun getTables() = listOf(ModBlocks.lootSupplier)

    override fun validate(map: Map<ResourceLocation, LootTable>, validationtracker: ValidationContext) {
        map.forEach { (location, lootTable) -> LootTables.validate(validationtracker, location, lootTable) }
    }
}